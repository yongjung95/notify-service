package com.example.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class GeminiService {

    private final ObjectMapper objectMapper;
    private final WebClient webClient;

    public GeminiService(WebClient.Builder webClientBuilder,
                         @Value("${gemini.api.url}") String geminiApiUrl,
                         @Value("${gemini.api.key}") String geminiApiKey) {
        this.objectMapper = new ObjectMapper();
        this.webClient = webClientBuilder.baseUrl(geminiApiUrl)
                .defaultHeader("x-goog-api-key", geminiApiKey)
                .build();
    }

    public Mono<String> callGeminiForSummary(String keyword, String fullText) {
        ObjectNode rootNode = objectMapper.createObjectNode();
        ArrayNode contentsArray = rootNode.putArray("contents");
        ObjectNode contentNode = contentsArray.addObject();
        ArrayNode partsArray = contentNode.putArray("parts");

        // 💡 프롬프트 엔지니어링: "다음 내용을 200자 내외로 요약해줘."

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
                .map(responseNode -> {
                    // 응답에서 요약된 텍스트를 안전하게 추출
                    JsonNode candidate = responseNode.path("candidates").path(0);
                    JsonNode part = candidate.path("content").path("parts").path(0);

                    return extractJsonFromText(part.path("text").asText(null));
                })
                .doOnError(e -> System.err.println("Gemini API 호출 에러: " + e.getMessage()))
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
