package com.example.schedule;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@Profile("!test")
public class KoreaStockUpdateSchedule {

    private final JobLauncher jobLauncher;


    private final Job koreaStockUpdateJob;

    public KoreaStockUpdateSchedule(JobLauncher jobLauncher, @Qualifier("koreaStockUpdateJob") Job koreaStockUpdateJob) {
        this.jobLauncher = jobLauncher;
        this.koreaStockUpdateJob = koreaStockUpdateJob;
    }

    @Scheduled(cron = "0 5 0 * * ?")
    public void reportCurrentTime() throws Exception {
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        List.of("kospi", "kosdaq").forEach(type -> {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .addString("type", type)
                    .addString("yesterday", yesterday)
                    .addLocalDateTime("runDateTime", LocalDateTime.now()) // 실행 시간 파라미터 추가
                    .toJobParameters();

            try {
                jobLauncher.run(koreaStockUpdateJob, jobParameters);
            } catch (Exception e) {
                log.info("❌ {} KOREA {} STOCK 업데이트 배치 실패", yesterday, type);
                log.error(e.getMessage());
            }
        });


    }
}
