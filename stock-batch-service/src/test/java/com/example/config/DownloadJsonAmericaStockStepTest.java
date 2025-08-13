package com.example.config;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class DownloadJsonAmericaStockStepTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private Step downloadJsonAmericaStockStep;

    @Test
    void downloadJsonAmericaStockStep_성공_테스트() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("today", "20250730")
                .addString("exchange", "nasdaq")
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // JobExecution 생성
        JobExecution jobExecution = jobRepository.createJobExecution("testJob", jobParameters);

        // StepExecution 생성
        StepExecution stepExecution = new StepExecution("downloadJsonAmericaStockStep", jobExecution);

        jobRepository.add(stepExecution);

        // when
        downloadJsonAmericaStockStep.execute(stepExecution);

        // then
        assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);

        // ExecutionContext에 파일 경로가 잘 저장되었는지 검증
        String savedFilePath = stepExecution.getJobExecution().getExecutionContext().getString("jsonFilePath");
        assertThat(savedFilePath).isNotNull();
        assertThat(Files.exists(Path.of(savedFilePath))).isTrue();
    }

}