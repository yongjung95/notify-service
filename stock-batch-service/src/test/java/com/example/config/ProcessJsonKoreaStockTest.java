package com.example.config;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ProcessJsonKoreaStockTest {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private Step processJsonKoreaStockStep;

    @Test
    void processJsonKoreaStockStep_성공_테스트() throws Exception {
        // given
        String filePath = new ClassPathResource("korea-stock-kospi.json").getFile().getAbsolutePath();

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("time", System.currentTimeMillis())
                .toJobParameters();

        // JobExecution 생성
        JobExecution jobExecution = jobRepository.createJobExecution("testJob", jobParameters);

        // JobExecution의 ExecutionContext에 파일 경로 설정
        jobExecution.getExecutionContext().putString("jsonFilePath", filePath);

        // StepExecution 생성
        StepExecution stepExecution = new StepExecution("processJsonKoreaStockStep", jobExecution);

        // StepExecution의 ExecutionContext에도 설정
        stepExecution.getExecutionContext().putString("jsonFilePath", filePath);

        jobRepository.add(stepExecution);

        // when
        processJsonKoreaStockStep.execute(stepExecution);

        // then
        assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(stepExecution.getReadCount()).isEqualTo(2);
    }
}
