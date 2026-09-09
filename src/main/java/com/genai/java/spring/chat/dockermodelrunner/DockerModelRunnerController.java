package com.genai.java.spring.chat.dockermodelrunner;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/docker-model-runner/chat")
public class DockerModelRunnerController {

    private static final String SYSTEM_PROMPT = "You are a helpful assistant that generate professional Linkedin posts about technical subjects."+
        "Ensure the posts are engaging, informative, and tailored to a professional audience. " +
            "Use a friendly and approachable tone while maintaining professionalism.";

    private final ChatClient chatClient;

    public DockerModelRunnerController(@Qualifier("openAIChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @PostMapping("/linkedin-post-generator")
    public String generateLinkedinPost(@RequestBody String message) {
        String response = chatClient.prompt()
                .user(message)
                .call()
                .content();
        return response;
    }
}
