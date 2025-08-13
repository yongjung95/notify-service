package com.example.config;

import com.example.domain.Stocks;
import com.example.repository.StocksRepository;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AmericaEtfUpdateBatchConfigTest {

    @Autowired
    @Qualifier("americaEtfUpdateJob")
    private Job americaEtfUpdateJob;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private StocksRepository stocksRepository;

    @Test
    void 미국_ETF_주식_업데이트_JOB_실행() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "20250730")
                .addString("exchange", "nasdaq")
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .addLocalDateTime("runDateTime", LocalDateTime.now()) // 실행 시간 파라미터 추가
                .toJobParameters();

        // when
        JobExecution jobExecution = jobLauncher.run(americaEtfUpdateJob, jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        Optional<Stocks> result = stocksRepository.findByTicker("QQQ");
        assertThat(result.get().getName()).isEqualTo("Invesco QQQ Trust");
        assertThat(result).isNotEmpty();
    }
}