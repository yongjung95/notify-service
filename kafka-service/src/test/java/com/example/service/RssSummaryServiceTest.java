package com.example.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RssSummaryServiceTest {

    @Autowired
    private RssSummaryService rssSummaryService;

    @Test
    void RSS_요약_요청() throws Exception {
        //given
        String keyword = "삼성전자";

        //when
        String result = rssSummaryService.summarizeRssFeed(keyword).block();

        //then
        System.out.println("result = " + result);

    }

    @Test
    void RSS_요약_에러() throws Exception {
        //given
        String keyword = "";

        //when
        String result = rssSummaryService.summarizeRssFeed(keyword).block();

        //then
        System.out.println("result = " + result);

    }
}
