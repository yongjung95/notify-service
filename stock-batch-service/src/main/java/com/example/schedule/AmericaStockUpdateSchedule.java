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
public class AmericaStockUpdateSchedule {

    private final JobLauncher jobLauncher;

    private final Job americaStockUpdateJob;

    public AmericaStockUpdateSchedule(JobLauncher jobLauncher, @Qualifier("americaStockUpdateJob") Job americaStockUpdateJob) {
        this.jobLauncher = jobLauncher;
        this.americaStockUpdateJob = americaStockUpdateJob;
    }

    @Scheduled(cron = "0 15 0 * * ?")
    public void reportCurrentTime() throws Exception {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        List.of("nasdaq", "amex", "nyse").forEach(exchange -> {
            log.info("✅ AMERICA {} STOCK 업데이트 배치 시작", exchange);

            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("today", today)
                    .addString("exchange", exchange)
                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
                    .addLocalDateTime("runDateTime", LocalDateTime.now()) // 실행 시간 파라미터 추가
                    .toJobParameters();

            try {
                jobLauncher.run(americaStockUpdateJob, jobParameters);
            } catch (Exception e) {
                log.error("❌ {} AMERICA {} STOCK 업데이트 배치 실패", today, exchange);
                log.error(e.getMessage());
            }
        });
    }
}
