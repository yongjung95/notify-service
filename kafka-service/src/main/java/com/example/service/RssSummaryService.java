package com.example.service;

import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RssSummaryService {

    private final GeminiService geminiService;

    @Cacheable(value = "news", key = "#keyword")
    public Mono<String> summarizeRssFeed(String keyword) {
        String rssUrl = "https://news.google.com/rss/search?q=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8) + "&hl=ko&gl=KR&ceid=KR:ko";

        return Mono.fromCallable(() -> {
                    URL feedSource = new URL(rssUrl);
                    SyndFeedInput input = new SyndFeedInput();
                    SyndFeed feed = input.build(new InputStreamReader(feedSource.openStream()));
                    // 구분자 추가

                    String fullText = feed.getEntries().stream()
                            .map(entry -> entry.getTitle() + "\n" + entry.getDescription().getValue())
                            .collect(Collectors.joining("\n\n---\n\n"));

                    return fullText;
                })
                .subscribeOn(Schedulers.boundedElastic()) // 블로킹 파싱 작업을 전용 스레드에서
                .flatMap(fullText -> geminiService.callGeminiForSummary(keyword, fullText))
                .doOnError(Throwable::printStackTrace)
                .onErrorReturn("뉴스 요약에 실패했습니다.");
    }
}
