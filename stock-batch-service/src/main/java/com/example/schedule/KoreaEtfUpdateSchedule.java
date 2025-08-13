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

@Slf4j
@Component
@Profile("!test")
public class KoreaEtfUpdateSchedule {

    private final JobLauncher jobLauncher;


    private final Job koreaEtfUpdateJob;

    public KoreaEtfUpdateSchedule(JobLauncher jobLauncher, @Qualifier("koreaEtfUpdateJob") Job koreaEtfUpdateJob) {
        this.jobLauncher = jobLauncher;
        this.koreaEtfUpdateJob = koreaEtfUpdateJob;
    }

    @Scheduled(cron = "0 10 0 * * ?")
//    @PostConstruct
    public void reportCurrentTime() throws Exception {
        String yesterday = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("JobID", String.valueOf(System.currentTimeMillis()))
                .addString("yesterday", yesterday)
                .addLocalDateTime("runDateTime", LocalDateTime.now()) // 실행 시간 파라미터 추가
                .toJobParameters();

        try {
            jobLauncher.run(koreaEtfUpdateJob, jobParameters);
        } catch (Exception e) {
            log.error("❌ {} Korea ETF 업데이트 배치 실패", yesterday);
            log.error(e.getMessage());
        }
    }
}
