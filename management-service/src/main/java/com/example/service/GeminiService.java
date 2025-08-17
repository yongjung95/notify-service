package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GeminiService {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder,
                         @Value("${gemini.api.url}") String geminiApiUrl,
                         @Value("${gemini.api.key}") String geminiApiKey) {
        this.objectMapper = new ObjectMapper();
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMinutes(3))      // 응답 타임아웃
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60000)  // 연결 타임아웃 60초
                .doOnConnected(conn ->
                        conn.addHandlerLast(new ReadTimeoutHandler(180, TimeUnit.SECONDS))  // 읽기 타임아웃 3분
                                .addHandlerLast(new WriteTimeoutHandler(180, TimeUnit.SECONDS)) // 쓰기 타임아웃 3분
                );

        this.webClient = webClientBuilder
                .baseUrl(geminiApiUrl)
                .defaultHeader("x-goog-api-key", geminiApiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .build();
    }

    public Mono<String> callGeminiForSummary(String keyword, String fullText) {
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode contentsArray = rootNode.putArray("contents");
        ObjectNode contentNode = contentsArray.addObject();
        ArrayNode partsArray = contentNode.putArray("parts");

        System.out.println("제미나이 호출");

        String promptTemplate = """
                다음 뉴스 내용을 분석하여 요약해줘.
                %s에 정확하게 일치하는 뉴스들만 요약해줘.
                아래 예시와 같은 JSON 형식으로 출력해줘.
                이모티콘을 사용해서 핵심이나 강조하고 싶은 곳에 사용해줘.
                답변하기 전에 단계적으로 내용을 정리해줘.
                
                ---
                %s
                ---
                
                {
                  "topics": [
                    {
                      "title": "주제1 제목",
                      "summary": "주제1 요약 내용"
                    },
                    {
                      "title": "주제2 제목",
                      "summary": "주제2 요약 내용"
                    }
                  ]
                }
                """;

        // 💡 String.format으로 템플릿에 뉴스 내용을 삽입
        String finalPrompt = String.format(promptTemplate, keyword, fullText);
        partsArray.addObject().put("text", finalPrompt);

        return webClient.post()
                .bodyValue(rootNode.toString())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnNext(responseNode -> {
                    log.info("Gemini API 응답 수신 성공");
                    log.debug("Gemini API 응답: {}", responseNode.toString());
                })
                .map(responseNode -> {
                    // 응답에서 요약된 텍스트를 안전하게 추출
                    JsonNode candidate = responseNode.path("candidates").path(0);
                    JsonNode part = candidate.path("content").path("parts").path(0);

                    String responseText = part.path("text").asText(null);
                    log.info("Gemini 응답 텍스트 길이: {}", responseText != null ? responseText.length() : 0);

                    return extractJsonFromText(responseText);
                })
                .doOnError(e -> {
                    System.err.println("Gemini API 호출 에러: " + e.getMessage());
                    if (e instanceof WebClientResponseException) {
                        WebClientResponseException webEx = (WebClientResponseException) e;
                        log.error("HTTP 상태: {}, 응답 바디: {}", webEx.getStatusCode(), webEx.getResponseBodyAsString());
                    }
                })
                .onErrorReturn("제미나이 API 호출 중 문제가 발생했습니다.");
    }

    private String extractJsonFromText(String rawText) {
        Pattern pattern = Pattern.compile("(?s)\\{.*\\}");
        Matcher matcher = pattern.matcher(rawText);

        if (matcher.find()) {
            return matcher.group();
        }
        throw new IllegalArgumentException();
    }
}
