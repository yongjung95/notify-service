package com.example.config;

import com.example.config.reader.AmericaStockJsonObjectReader;
import com.example.domain.Stocks;
import com.example.domain.StocksType;
import com.example.dto.AmericaStockDTO;
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
public class AmericaStockUpdateBatchConfig {

    @Value("${batch.america.api.stock-url}")
    private String americaApiStockUrl;
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

    @Bean("americaStockUpdateJob")
    public Job americaStockUpdateJob(Step downloadJsonAmericaStockStep,
                                     Step processJsonAmericaStockStep
    ) {
        return new JobBuilder("americaStockUpdateJob", jobRepository)
                .start(downloadJsonAmericaStockStep)
                .next(processJsonAmericaStockStep)
                .build();
    }

    @Bean
    public Step downloadJsonAmericaStockStep(Tasklet downloadJsonAmericaStockTasklet
    ) {
        return new StepBuilder("downloadJsonAmericaStockStep", jobRepository)
                .tasklet(downloadJsonAmericaStockTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet downloadJsonAmericaStockTasklet(
            @Value("#{jobParameters['exchange']}") String exchange,
            @Value("#{jobParameters['today']}") String today
    ) {
        return (contribution, chunkContext) -> {
            log.info("✅ {} AMERICA {} STOCK JSON 파일 다운로드 시작...", today, exchange);

            // 주입받은 값들로 최종 API URL과 파일 경로를 동적으로 생성
            Path directoryPath = Paths.get(americaDownloadDir + "/" + today);
            Files.createDirectories(directoryPath);
            Path savedFilePath = directoryPath.resolve("america-stock-" + exchange + ".json");

            // WebClient로 다운로드 실행 (이하 로직은 동일)
            WebClient webClient = WebClient.builder()
                    .baseUrl(americaApiStockUrl)
                    .codecs(configurer ->
                            configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                    .build();

            byte[] fileBytes = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .queryParam("exchange", exchange)
                            .queryParam("tableonly", true)
                            .queryParam("download", true)
                            .build())
                    .retrieve()
                    .bodyToMono(byte[].class)
                    .block();

            if (fileBytes == null) {
                throw new IOException("다운로드 실패");
            }

            Files.write(savedFilePath, fileBytes);
            log.info("✅ 파일 다운로드 완료: {}", savedFilePath);

            // 다음 스텝에 파일 경로 전달
            contribution.getStepExecution().getJobExecution().getExecutionContext()
                    .putString("jsonFilePath", savedFilePath.toAbsolutePath().toString());

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step processJsonAmericaStockStep(JsonItemReader<AmericaStockDTO> jsonAmericaStockItemReader,
                                           ItemProcessor<AmericaStockDTO, Stocks> americaStockProcessor,
                                           JpaItemWriter<Stocks> americaStockWriter
    ) {
        return new StepBuilder("processJsonAmericaStockStep", jobRepository)
                .<AmericaStockDTO, Stocks>chunk(100, transactionManager)
                .reader(jsonAmericaStockItemReader)
                .processor(americaStockProcessor)
                .writer(americaStockWriter)
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<AmericaStockDTO> jsonAmericaStockItemReader(
            @Value("#{jobExecutionContext['jsonFilePath']}") String filePath
    ) {
        return new JsonItemReaderBuilder<AmericaStockDTO>()
                .name("jsonAmericaStockItemReader")
                .resource(new FileSystemResource(filePath))
                .jsonObjectReader(new AmericaStockJsonObjectReader())
                .build();
    }

    @Bean
    @StepScope
    public ItemProcessor<AmericaStockDTO, Stocks> americaStockProcessor(
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
                    exchange.toUpperCase(), StocksType.STOCK);
        };
    }

    /**
     * ItemWriter: 처리된 Entity 리스트를 DB에 저장하는 역할
     */
    @Bean
    public JpaItemWriter<Stocks> americaStockWriter() {
        return new JpaItemWriterBuilder<Stocks>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
