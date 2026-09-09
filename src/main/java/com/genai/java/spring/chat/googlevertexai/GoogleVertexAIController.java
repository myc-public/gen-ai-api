package com.genai.java.spring.chat.googlevertexai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vertexai/chat")
public class GoogleVertexAIController {
    private final static String SYSTEM_PROMPT = "You are a helpful assistant that generates professional JavaDoc comments for Java code." +
            "Always explain the purpose of the class or method, its parameters, return values, and any exceptions it may throw." +
            "Use the standard JavaDoc style with /** ... */." +
            "Keep explanations concise, clear, and technical.";
    private static final Logger log = LoggerFactory.getLogger(GoogleVertexAIController.class);

    private final ChatClient chatClient;

    public GoogleVertexAIController(@Qualifier("vertexAIChatClient")ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/generate-java-docs")
    public ChatClientResponse generateJavaDocs(@RequestBody String message) {

        ChatClientResponse response = chatClient.prompt()
                .user(message)
                .system(SYSTEM_PROMPT)
                .call()
                .chatClientResponse();
        log.info("JavaDoc generation response: {}", response.toString());
        return response;
    }
}