package com.email.writer.service;

import com.email.writer.Entity.EmailRequestEntity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class EmailGeneratorService {

    private final WebClient webClient;
    private final String apiKey;

    public EmailGeneratorService(WebClient.Builder webClientBuilder,
                                 @Value("${gemini.api.url}") String baseUrl,
                                 @Value("${gemini.api.key}") String geminiApiKey) {
        this.apiKey = geminiApiKey;
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    public String generateEmailReply(EmailRequestEntity emailRequest) {
        // Build the prompt
        String prompt = buildPrompt(emailRequest);

        // Prepare the request body
        String requestBody = String.format("""
                {
                    "contents": [
                        {
                            "parts": [
                                {
                                    "text": "%s"
                                }
                            ]
                        }
                    ]
                }""", prompt);

        // Call the Gemini API
        String response = webClient.post()
                .uri("/v1beta/models/gemini-2.0-flash:generateContent")
                .header("x-goog-api-key", apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        // Extract and return the generated email
        return extractResponseContent(response);
    }

    private String extractResponseContent(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse Gemini API response", e);
        }
    }

    private String buildPrompt(EmailRequestEntity emailRequest) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Generate a professional and concise email reply based on the message below. ");
        prompt.append("Maintain a polite tone, address any questions, and include necessary actions or acknowledgments. ");
        prompt.append("Do not include a subject line.");

        if (emailRequest.getTone() != null && !emailRequest.getTone().isEmpty()) {
            prompt.append(" Use this tone: ").append(emailRequest.getTone()).append(".");
        }

        prompt.append("\nHere is the original email:\n").append(emailRequest.getEmailContent());
        return prompt.toString();
    }
}
