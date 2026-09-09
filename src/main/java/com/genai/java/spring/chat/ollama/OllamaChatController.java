package com.genai.java.spring.chat.ollama;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ollama/chat")
public class OllamaChatController {

    private static final Logger log = LoggerFactory.getLogger(OllamaChatController.class);
    private final ChatClient chatClient;

    private final static String SYSTEM_PROMPT = "You are a helpful professional email assistant. Draft a clear, concise, and professional email based on user input. " +
            "Ensure the emails are clear, polite, and tailored to the specified context. " +
            "Use a formal and respectful tone while maintaining brevity.";

    public OllamaChatController (@Qualifier("ollamaChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/draft-email")
    public String draftEmail(@RequestBody String message) {

        log.info("start Received message for email drafting: {}", message );
        String response = chatClient.prompt()
             //   .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .content();
        return response;
    }
}
