package com.example.service.impl;

import com.example.domain.NewsData;
import com.example.dto.NewsManagementNotify;
import com.example.dto.SendNotifyNewsTopic;
import com.example.repository.NewsDataRepository;
import com.example.repository.NewsManagementQueryRepository;
import com.example.service.GeminiService;
import com.example.service.KafkaProducerService;
import com.example.service.NewsManagementService;
import com.example.service.RssSummaryService;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class NewsManagementServiceImplTest {

    @Autowired
    private NewsManagementService newsManagementService;

    @MockitoBean
    private GeminiService geminiService;
    @MockitoBean
    private RssSummaryService rssSummaryService;
    @MockitoBean
    private KafkaProducerService kafkaProducerService;
    @MockitoBean
    private NewsManagementQueryRepository newsManagementQueryRepository;
    @MockitoBean
    private NewsDataRepository newsDataRepository;

    @Mock
    private NewsManagementNotify newsManagementNotify;

    @Test
    void KAFKA_메시지_발행_테스트() throws Exception {
        //given
        when(newsManagementQueryRepository.findNewsManagementNotify()).thenReturn(List.of(newsManagementNotify));

        when(newsManagementNotify.keyword()).thenReturn("삼성전자");
        when(newsManagementNotify.memberUUID()).thenReturn("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");
        when(newsManagementNotify.nickname()).thenReturn("testuser1");
        when(newsManagementNotify.fcmToken()).thenReturn("fcm-token-1");

        NewsData mockedNewsData = mock(NewsData.class);
        when(mockedNewsData.getId()).thenReturn(1L);
        when(newsDataRepository.save(any(NewsData.class))).thenReturn(mockedNewsData);

        when(geminiService.callGeminiForSummary(eq("삼성전자"), any(String.class)))
                .thenReturn(Mono.just("""
                        {
                          "topics": [
                            {
                              "title": "📱 갤럭시 신제품 및 OS 업데이트",
                              "summary": "삼성전자가 갤럭시 S26 라인업을 재편하며 '엣지' 디자인을 합류시킬 예정입니다. 🚀 9월에는 '갤럭시 S25 FE'를 출시하고, 기존 '갤럭시 Z 폴드6' 등 모델에는 One UI 8 베타 프로그램을 확대 운영하여 사용자 경험을 개선하고 있습니다. 🎶 또한, '갤럭시 S25 시리즈'가 국내 판매 300만 대를 돌파하며 견조한 실적을 보이고 있습니다."
                            }
                          ]
                        }
                        """));
        when(rssSummaryService.summarizeRssFeed(eq("삼성전자")))
                .thenReturn(Mono.just("""
                        <rss xmlns:media="http://search.yahoo.com/mrss/" version="2.0">
                            <channel>
                            <generator>NFE/5.0</generator>
                            <title>"삼성전자" - Google 뉴스</title>
                            <link>https://news.google.com/search?q=%EC%82%BC%EC%84%B1%EC%A0%84%EC%9E%90&hl=ko&gl=KR&ceid=KR:ko</link>
                            <language>ko</language>
                            <webMaster>news-webmaster@google.com</webMaster>
                            <copyright>Copyright © 2025 Google. All rights reserved. This XML feed is made available solely for the purpose of rendering Google News results within a personal feed reader for personal, non-commercial use. Any other use of the feed is expressly prohibited. By accessing this feed or using these results in any manner whatsoever, you agree to be bound by the foregoing restrictions.</copyright>
                            <lastBuildDate>Wed, 06 Aug 2025 01:51:12 GMT</lastBuildDate>
                            <image>
                                <title>Google 뉴스</title>
                                <url>https://lh3.googleusercontent.com/-DR60l-K8vnyi99NZovm9HlXyZwQ85GMDxiwJWzoasZYCUrPuUM_P_4Rb7ei03j-0nRs0c4F=w256</url>
                                <link>https://news.google.com/</link>
                                <height>256</height>
                                <width>256</width>
                            </image>
                        <description>Google 뉴스</description>
                        <item>
                            <title>삼성전자 HBM3E 12단 엔비디아 인증 빨라야 4분기, HBM4 인증·수익성 확보 절실 - 비즈니스포스트</title>
                            <link>https://news.google.com/rss/articles/CBMic0FVX3lxTE1JM2dZaTBvLW5oOVA5dXVfdDFkVlJJN2ZqTDRDRU5zbU9xdV93dnZ5RXYyOHpzYjhuLTREWi14YjVWM0F2ZVVoeVBsQnNxV0JNN1I3ZTZOcDBTcFVkVlpmMEljM2JfUWoxR19Ga19CZmNDOUk?oc=5</link>
                            <guid isPermaLink="false">CBMic0FVX3lxTE1JM2dZaTBvLW5oOVA5dXVfdDFkVlJJN2ZqTDRDRU5zbU9xdV93dnZ5RXYyOHpzYjhuLTREWi14YjVWM0F2ZVVoeVBsQnNxV0JNN1I3ZTZOcDBTcFVkVlpmMEljM2JfUWoxR19Ga19CZmNDOUk</guid>
                            <pubDate>Wed, 06 Aug 2025 00:15:29 GMT</pubDate>
                            <description><ol><li><a href="https://news.google.com/rss/articles/CBMic0FVX3lxTE1JM2dZaTBvLW5oOVA5dXVfdDFkVlJJN2ZqTDRDRU5zbU9xdV93dnZ5RXYyOHpzYjhuLTREWi14YjVWM0F2ZVVoeVBsQnNxV0JNN1I3ZTZOcDBTcFVkVlpmMEljM2JfUWoxR19Ga19CZmNDOUk?oc=5" target="_blank">삼성전자 HBM3E 12단 엔비디아 인증 빨라야 4분기, HBM4 인증·수익성 확보 절실</a>&nbsp;&nbsp;<font color="#6f6f6f">비즈니스포스트</font></li><li><a href="https://news.google.com/rss/articles/CBMicEFVX3lxTFB3R2tPVzlINC1yLWRwb0VMcFMxNW9Vdk1CLTJEVEI5X1QwaENlN1BiODg4THAyRmFQLUJfTGgzRGo0OW1BODczRFhVWDFHSk5NR19PQ05aQTEzZEtKRDhEeXF2TVhiT1FqbWZtSVBxeGnSAXRBVV95cUxNQ2FOQ0FoRGRFT2M0ZWl6WlRJYU5kMTByVG1qc1BteUM1U1pnNnp6cFp5cVVuQzRXMHUwUTVrY2dmUzVJajRTSmJUYmlIYXdzX1NPWGtXbDdhRkI2b2Jsck5SM0F2dWJzZEx1TGRfNlQxajZIRw?oc=5" target="_blank">삼성전자, 테슬라 AI칩 수주로 파운드리 반등…HBM은 엔비디아 퀄테스트에 발목</a>&nbsp;&nbsp;<font color="#6f6f6f">IT조선</font></li><li><a href="https://news.google.com/rss/articles/CBMiYEFVX3lxTE9iejBMaUZoRnZzcTFONGdpendGX1ZDWTI0aElGcE03Nmx0UVU4eGdXT3BwVGFBTElKLXZFNnNIbHo2X1NCbXQ5M0FvMFJ4Y3YwSlhvV2VBUm5UaWxHRDNCN9IBeEFVX3lxTE5xTUhRWHY0U09kdDVSN3JabE93VDdiX2lHLW9VRWZ5cGF3UUF3QjNRcG1PMlotbDU0RkpnWmx0ejJ6TVZ4eFZBRVVhU0FKaGMtT3d3S3lTYldweXhmaWtaQ2s5dGdwLUszLWpoVXNNX0wxc29Vb3dzRA?oc=5" target="_blank">"땡큐 엔비디아"…삼성전자, 반도체주 훈풍에 '7만전자' 복귀[핫스탁]</a>&nbsp;&nbsp;<font color="#6f6f6f">뉴시스</font></li></ol></description>
                            <source url="https://www.businesspost.co.kr">비즈니스포스트</source>
                        </item>
                        </channel>
                        </rss>
                        """));

        //when
        Mono<Void> resultMono = newsManagementService.sendNewsManagement();

        //then
        StepVerifier.create(resultMono)
                .verifyComplete();

        verify(kafkaProducerService, times(1)).sendTopicMessage(
                eq("news-management-topic"),
                eq("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d"),
                any(SendNotifyNewsTopic.class)
        );
    }
}