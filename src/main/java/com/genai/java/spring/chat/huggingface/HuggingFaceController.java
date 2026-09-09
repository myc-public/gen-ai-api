package com.genai.java.spring.chat.huggingface;

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
@RequestMapping("/api/huggingface/chat")
public class HuggingFaceController {

    private final static String SYSTEM_PROMPT = "You are a senior engineer. Generate code based on the description. "
    + "Ensure the code is idiomatic, efficient, well-structured, follows best practices. ";

    private static final Logger log = LoggerFactory.getLogger(HuggingFaceController.class);

    private final ChatClient chatClient;

    public HuggingFaceController(@Qualifier("openAIChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/generate-code")
    public ChatClientResponse generateCode(@RequestBody String message) {
        ChatClientResponse response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .chatClientResponse();

        log.info("Generated code for message: {}", response.chatResponse().toString());
        return  response;

    }
}
