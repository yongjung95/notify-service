package com.example.config;

import com.example.config.reader.KoreaEtfJsonObjectReader;
import com.example.domain.Stocks;
import com.example.domain.StocksType;
import com.example.dto.KoreaEtfDTO;
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
public class KoreaEtfUpdateBatchConfig {

    @Value("${batch.korea.api.etf-url}")
    private String koreaApiEtfUrl;
    @Value("${batch.korea.api.token}")
    private String koreaApiToken;
    @Value("${batch.korea.file.download-dir}")
    private String koreaFileDownloadDir;

    private final StocksRepository stocksRepository;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean("koreaEtfUpdateJob")
    public Job koreaEtfUpdateJob(Step downloadJsonKoreaEtfStep,
                                     Step processJsonKoreaEtfStep
    ) {
        return new JobBuilder("koreaEtfUpdateJob", jobRepository)
                .start(downloadJsonKoreaEtfStep)
                .next(processJsonKoreaEtfStep)
                .build();
    }

    @Bean
    public Step downloadJsonKoreaEtfStep(Tasklet downloadJsonKoreaEtfTasklet
    ) {
        return new StepBuilder("downloadJsonKoreaEtfStep", jobRepository)
                .tasklet(downloadJsonKoreaEtfTasklet, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet downloadJsonKoreaEtfTasklet(
            @Value("#{jobParameters['yesterday']}") String yesterday
    ) {
        return (contribution, chunkContext) -> {
            log.info("✅ {} KOREA ETF JSON 파일 다운로드 시작...", yesterday);

            Path directoryPath = Paths.get(koreaFileDownloadDir + "/" + yesterday);
            Files.createDirectories(directoryPath);
            Path savedFilePath = directoryPath.resolve("korea-etf.json");

            // 4. WebClient로 다운로드 실행 (이하 로직은 동일)
            WebClient webClient = WebClient.builder()
                    .baseUrl(koreaApiEtfUrl)
                    .codecs(configurer ->
                            configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024))
                    .build();

            byte[] fileBytes = webClient.get()
                    .uri(uriBuilder -> uriBuilder
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

            // 5. 다음 스텝에 파일 경로 전달
            contribution.getStepExecution().getJobExecution().getExecutionContext()
                    .putString("jsonFilePath", savedFilePath.toAbsolutePath().toString());

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Step processJsonKoreaEtfStep(JsonItemReader<KoreaEtfDTO> koreaEtfjsonItemReader,
                                               ItemProcessor<KoreaEtfDTO, Stocks> koreaEtfProcessor,
                                               JpaItemWriter<Stocks> koreaEtfWriter
    ) {
        return new StepBuilder("processJsonKoreaEtfStep", jobRepository)
                .<KoreaEtfDTO, Stocks>chunk(100, transactionManager)
                .reader(koreaEtfjsonItemReader)
                .processor(koreaEtfProcessor)
                .writer(koreaEtfWriter)
                .build();
    }

    @Bean
    @StepScope
    public JsonItemReader<KoreaEtfDTO> koreaEtfjsonItemReader(
            @Value("#{jobExecutionContext['jsonFilePath']}") String filePath
    ) {
        return new JsonItemReaderBuilder<KoreaEtfDTO>()
                .name("jsonItemReader")
                .resource(new FileSystemResource(filePath))
                .jsonObjectReader(new KoreaEtfJsonObjectReader())
                .build();
    }

    @Bean
    public ItemProcessor<KoreaEtfDTO, Stocks> koreaEtfProcessor() {
        return koreaEtfDTO -> {
            if (!StringUtils.hasText(koreaEtfDTO.getStockCode())) {
                return null;
            }
            if (stocksRepository.findByTicker(koreaEtfDTO.getStockCode()).isEmpty()) {
                return Stocks.of(koreaEtfDTO.getStockCode(), koreaEtfDTO.getCorpName(),
                        "KOREA", "KRX", StocksType.ETF);
            }

            return null;
        };
    }

    /**
     * ItemWriter: 처리된 Entity 리스트를 DB에 저장하는 역할
     */
    @Bean
    public JpaItemWriter<Stocks> koreaEtfWriter() {
        return new JpaItemWriterBuilder<Stocks>()
                .entityManagerFactory(entityManagerFactory)
                .build();
    }
}
