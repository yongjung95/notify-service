package com.example.config;

import com.example.config.reader.KoreaStockJsonObjectReader;
import com.example.domain.Stocks;
import com.example.domain.StocksType;
import com.example.dto.KoreaStockDTO;
import com.example.repository.StocksRepository;
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

@Slf4j
@Configuration
@RequiredArgsConstructor
public class KoreaStockUpdateBatchConfig {

    @Value("${batch.korea.api.stock-url}")
    private String koreaApiStockUrl;

    @Value("${batch.korea.api.token}")
    private String koreaApiToken;

    @Value("${batch.korea.file.download-dir}")
    private String koreaFileDownloadDir;

    private final StocksRepository stocksRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;


    @Bean("koreaStockUpdateJob")
    public Job koreaStockUpdateJob(Step downloadJsonKoreaStockStep,
                                   Step processJsonKoreaStockStep) throws IOException {
        return new JobBuilder("koreaStockUpdateJob", jobRepository)
                .start(downloadJsonKoreaStockStep)
                .next(processJsonKoreaStockStep)
                .build();
    }

    @Bean
    public Step downloadJsonKoreaStockStep(Tasklet downloadJsonKoreaStockTasklet) {
        return new StepBuilder("downloadJsonKoreaStockStep", jobRepository)
                .tasklet(downloadJsonKoreaStockTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet downloadJsonKoreaStockTasklet(
            @Value("#{jobParameters['type']}") String type,
            @Value("#{jobParameters['yesterday']}") String yesterday
    ) {
        return (contribution, chunkContext) -> {
            log.info("✅ {} KOREA {} STOCK JSON 파일 다운로드 시작...", yesterday, type);

            String path = switch (type) {
                case "kospi" -> "/stk_isu_base_info";
                case "kosdaq" -> "/ksq_isu_base_info";
                default -> "";
            };

            // 주입받은 값들로 최종 API URL과 파일 경로를 동적으로 생성
            Path directoryPath = Paths.get(koreaFileDownloadDir + "/" + yesterday);
            Files.createDirectories(directoryPath);
            Path savedFilePath = directoryPath.resolve("korea-stock-" + type + ".json");

            // WebClient로 다운로드 실행 (이하 로직은 동일)
            WebClient webClient = WebClient.builder()
                    .baseUrl(koreaApiStockUrl)
                    .codecs(configurer ->
                            configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                    .build();

            byte[] fileBytes = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path(path)
                            .queryParam("basDd", yesterday)
                            .build())
                    .header("AUTH_KEY", koreaApiToken)
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
    public Step processJsonKoreaStockStep(JsonItemReader<KoreaStockDTO> jsonKoreaStockItemReader,
                                          ItemProcessor<KoreaStockDTO, Stocks> koreaStockProcessor,
                                          JpaItemWriter<Stocks> koreaStockWriter) {
        return new StepBuilder("processJsonKoreaStockStep", jobRepository)
                .<KoreaStockDTO, Stocks>chunk(100, transactionManager) // 100개씩 청크 처리
                .reader(jsonKoreaStockItemReader)
                .processor(koreaStockProcessor)
                .writer(koreaStockWriter)
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<KoreaStockDTO> jsonKoreaStockItemReader(
            @Value("#{jobExecutionContext['jsonFilePath']}") String filePath
    ) {
        return new JsonItemReaderBuilder<KoreaStockDTO>()
                .name("jsonKoreaStockItemReader")
                .resource(new FileSystemResource(filePath))
                .jsonObjectReader(new KoreaStockJsonObjectReader())
                .build();
    }

    @Bean
    public ItemProcessor<KoreaStockDTO, Stocks> koreaStockProcessor() {
        return koreaStockDTO -> {
            if (!StringUtils.hasText(koreaStockDTO.getStockCode())) {
                return null;
            }
            if (stocksRepository.findByTicker(koreaStockDTO.getStockCode()).isEmpty()) {
                return Stocks.of(koreaStockDTO.getStockCode(), koreaStockDTO.getCorpName(),
                        "KOREA", "KRX", StocksType.STOCK);
            }

            return null;
        };
    }

    @Bean
    public JpaItemWriter<Stocks> koreaStockWriter() {
        return new JpaItemWriterBuilder<Stocks>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
