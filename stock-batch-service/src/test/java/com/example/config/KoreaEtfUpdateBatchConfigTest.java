package com.example.config;

import com.example.domain.Stocks;
import com.example.repository.StocksRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class KoreaEtfUpdateBatchConfigTest {

    @Autowired
    private StocksRepository stocksRepository;

    @Autowired
    @Qualifier("koreaEtfUpdateJob")
    private Job koreaEtfUpdateJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private JobRepository jobRepository;

    @Test
    void 주식_업데이트_JOB_실행() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .addString("yesterday", "20250729")
                .addLocalDateTime("runDateTime", LocalDateTime.now()) // 실행 시간 파라미터 추가
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncher.run(koreaEtfUpdateJob, jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        Optional<Stocks> result = stocksRepository.findByTicker("360750");
        assertThat(result).isNotEmpty();
    }
}