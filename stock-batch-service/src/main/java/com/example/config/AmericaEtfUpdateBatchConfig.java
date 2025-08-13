package com.example.config;

import com.example.domain.Stocks;
import com.example.domain.StocksType;
import com.example.dto.AmericaEtfDTO;
import com.example.repository.StocksRepository;
import com.example.service.ApiInfoService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class AmericaEtfUpdateBatchConfig {

    @Value("${batch.america.api.etf-url}")
    private String americaApiEtfUrl;
    @Value("${batch.america.api.etf-token}")
    private String americaApiEtfToken;
    @Value("${batch.america.file.download-dir}")
    private String americaDownloadDir;
    @Value("${korea-invest.api.base-url}")
    private String koreaInvestBaseUrl;
    @Value("${korea-invest.api.app-key}")
    private String koreaInvestAppKey;
    @Value("${korea-invest.api.app-secret}")
    private String koreaInvestAppSecret;

    private final StocksRepository stocksRepository;
    private final ApiInfoService apiInfoService;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean("americaEtfUpdateJob")
    public Job americaEtfUpdateJob(Step downloadJsonAmericaEtfStep,
                                     Step processJsonAmericaEtfStep
    ) {
        return new JobBuilder("americaEtfUpdateJob", jobRepository)
                .start(downloadJsonAmericaEtfStep)
                .next(processJsonAmericaEtfStep)
                .build();
    }

    @Bean
    public Step downloadJsonAmericaEtfStep(Tasklet downloadJsonAmericaEtfTasklet
    ) {
        return new StepBuilder("downloadJsonAmericaEtfStep", jobRepository)
                .tasklet(downloadJsonAmericaEtfTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet downloadJsonAmericaEtfTasklet(
            @Value("#{jobParameters['exchange']}") String exchange,
            @Value("#{jobParameters['today']}") String today
    ) {
        return (contribution, chunkContext) -> {
            log.info("✅ {} AMERICA {} ETF JSON 파일 다운로드 시작...", today, exchange);

            Path directoryPath = Paths.get(americaDownloadDir + "/" + today);
            Files.createDirectories(directoryPath);
            Path savedFilePath = directoryPath.resolve("america-etf-" + exchange + ".json");

            // 4. WebClient로 다운로드 실행 (이하 로직은 동일)
            WebClient webClient = WebClient.builder()
                    .baseUrl(americaApiEtfUrl)
                    .codecs(configurer ->
                            configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                    .build();

            byte[] fileBytes = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + exchange)
                            .queryParam("api_token", americaApiEtfToken)
                            .queryParam("fmt", "json")
                            .queryParam("type", "etf")
                            .build())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (fileBytes == null) {
                throw new IOException("다운로드 실패");
            }

            Files.write(savedFilePath, fileBytes);
            log.info("✅ 파일 다운로드 완료: {}", savedFilePath);

            // 5. 다음 스텝에 파일 경로 전달
            contribution.getStepExecution().getJobExecution().getExecutionContext()
                    .putString("jsonFilePath", savedFilePath.toAbsolutePath().toString());

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step processJsonAmericaEtfStep(JsonItemReader<AmericaEtfDTO> americaEtfjsonItemReader,
                                               ItemProcessor<AmericaEtfDTO, Stocks> americaEtfProcessor,
                                               JpaItemWriter<Stocks> americaEtfWriter
    ) {
        return new StepBuilder("processJsonAmericaEtfStep", jobRepository)
                .<AmericaEtfDTO, Stocks>chunk(100, transactionManager)
                .reader(americaEtfjsonItemReader)
                .processor(americaEtfProcessor)
                .writer(americaEtfWriter)
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<AmericaEtfDTO> americaEtfjsonItemReader(
            @Value("#{jobExecutionContext['jsonFilePath']}") String filePath
    ) {
        return new JsonItemReaderBuilder<AmericaEtfDTO>()
                .name("jsonItemReader")
                .resource(new FileSystemResource(filePath))
                .jsonObjectReader(new JacksonJsonObjectReader<>(AmericaEtfDTO.class))
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<AmericaEtfDTO, Stocks> americaEtfProcessor(
            @Value("#{jobParameters['exchange']}") String exchange,
            @Value("#{jobParameters['today']}") String today
    ) {
        return dto -> {
            if (stocksRepository.findByTicker(dto.getSymbol()).isPresent()) {
                return null;
            }

            String token = apiInfoService.getToken(today);

            WebClient webClient = WebClient.builder()
                    .baseUrl(koreaInvestBaseUrl)
                    .build();

            String prdtTypeCd = switch (exchange) {
                case "amex" -> "529";
                case "nyse" -> "513";
                case "nasdaq" -> "512";
                default -> "";
            };

            String koreanName = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/uapi/overseas-price/v1/quotations/search-info")
                            .queryParam("PRDT_TYPE_CD", prdtTypeCd)
                            .queryParam("PDNO", dto.getSymbol())
                            .build())
                    .header("authorization", token)
                    .header("appkey", koreaInvestAppKey)
                    .header("appsecret", koreaInvestAppSecret)
                    .header("tr_id", "CTPF1702R")
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .mapNotNull(jsonNode -> {
                        String name = jsonNode.path("output").path("prdt_name").asText();
                        return StringUtils.hasText(name) ? name : null;
                    })
                    .timeout(Duration.ofSeconds(10))
                    .retry(2)
                    .onErrorReturn("")
                    .block();

            if (!StringUtils.hasText(koreanName)) {
                return null;
            }

            return Stocks.of(dto.getSymbol(), koreanName, "AMERICA",
                    exchange.toUpperCase(), StocksType.ETF);
        };
    }

    /**
     * ItemWriter: 처리된 Entity 리스트를 DB에 저장하는 역할
     */
    @Bean
    public JpaItemWriter<Stocks> americaEtfWriter() {
        return new JpaItemWriterBuilder<Stocks>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
