package com.example.schedule;

import com.example.service.ApiInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@Profile("!test")
public class ApiInfoSchedule {

    private final ApiInfoService apiInfoService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleStockApiInfo() throws Exception {
        apiInfoService.getApiInfo();
    }

}
