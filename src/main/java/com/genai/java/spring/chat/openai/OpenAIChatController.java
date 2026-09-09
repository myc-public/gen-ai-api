package com.genai.java.spring.chat.openai;


import com.genai.java.spring.chat.openai.dto.response.SummarizationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@RequestMapping("/api/openai/chat")
@RestController
public class OpenAIChatController {


    private final static String SYSTEM_PROMPT = "You are a helpful assistant that summarize any given content. " +
            "Ensure the summary is concise, informative, and captures the key points. " +
            "Use a friendly and approachable tone while maintaining professionalism." +
            "Do not answer anything other than the summarization. If the question is not about summarization," +
            "respond with 'I can only help with summarization tasks.'";
    private static final Logger log = LoggerFactory.getLogger(OpenAIChatController.class);
    private final ChatClient chatClient;

    private final OpenAIService openAIService;


    public OpenAIChatController(@Qualifier("openAIChatClient") ChatClient chatClient, OpenAIService openAIService) {
        this.chatClient = chatClient;
        this.openAIService = openAIService;
    }

    @PostMapping("/summarize-content")
    public String summarizeContent(@RequestBody String message) {
        // Use the chatClient to summarize the text
        // This is a placeholder for the actual implementation
        String response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .content();
        log.info("Summarization content: {}", response);
        return response;

    }

    @PostMapping("/summarize-chat-response")
    public ChatResponse summarizeResponse(@RequestBody String message) {
        ChatResponse response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .chatResponse();
        log.info("Summarization chat response: {}", response.toString());
        return response;

    }


    @PostMapping("/summarize-chat-client-response")
    public ChatClientResponse summarizeClientResponse(@RequestBody String message) {
        ChatClientResponse response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .call()
                .chatClientResponse();
        log.info("Summarization chat client response: {}", response.toString());
        return response;

    }

    @PostMapping(value = "/summarize-with-stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> summarizeWithStream(@RequestBody String message) {
        Flux<String> response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(message)
                .stream()
                .content()
                .bufferTimeout(2, Duration.ofMillis(2900)) //40 tokens or every 200ms, whichever comes first
                        .map(tokenList -> String.join(",", tokenList));
        log.info("Summarization with stream: {}", response.toString());
        return response;

    }



    @PostMapping("/summarize-meeting-notes")
    public String summarizeMeetingNotes(@RequestBody String meetingNotes) {
        if (meetingNotes == null || meetingNotes.isBlank()) {
            throw new IllegalArgumentException("meetingNotes cannot be null or blank");
        }

        String response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(u -> u.text("Can you summarize the following meeting notes: {meetingNotes}" +
                                " Use the format as described in the following example while doing the summarization:" +
                                " Input: In today’s sales strategy meeting, we reviewed Q3 targets and performance gaps. The team agreed to focus on enterprise clients and strengthen partnerships." +
                                " A proposal was made to expand into two new regions. Marketing suggested aligning campaigns with sales objectives to improve lead conversion and shorten sales cycles." +
                                " Output:" +
                                " Action Items:" +
                                "* Focus on enterprise clients and partnerships." +
                                "* Explore expansion into two new regions." +
                                "* Align marketing campaigns with sales objectives." +
                                " Decisions:" +
                                "* Enterprise clients prioritized for Q3." +
                                "* Marketing and sales to work jointly on lead conversion.")
                        .param("meetingNotes", meetingNotes))
                .call()
                .content();
        log.info("Summarization meeting notes: {}", response);
        return response;
    }
    @PostMapping("/summarize-meeting-notes-structured")
    public SummarizationResponse summarizeMeetingNotesStructuredOutput(@RequestBody String meetingNotes) {

        SummarizationResponse response = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(u -> u.text("Can you summarize the following meeting notes: {meetingNotes}" +
                                " Use the format as described in the following example while doing the summarization:" +
                                " Input: In today’s sales strategy meeting, we reviewed Q3 targets and performance gaps. The team agreed to focus on enterprise clients and strengthen partnerships." +
                                " A proposal was made to expand into two new regions. Marketing suggested aligning campaigns with sales objectives to improve lead conversion and shorten sales cycles." +
                                " Output:" +
                                " Action Items:" +
                                "* Focus on enterprise clients and partnerships." +
                                "* Explore expansion into two new regions." +
                                "* Align marketing campaigns with sales objectives." +
                                " Decisions:" +
                                "* Enterprise clients prioritized for Q3." +
                                "* Marketing and sales to work jointly on lead conversion.")
                        .param("meetingNotes", meetingNotes))
                .call()
                .entity(
                        SummarizationResponse.class
                );
        log.info("Summarization meeting notes structured: {}", response.toString());

        return response;
    }



    @PostMapping("/summarize-meeting-notes-structured-list")
    public List<SummarizationResponse> summarizeMeetingNotesStructuredOutputList(@RequestBody String meetingNotes) {
        try {
            List<SummarizationResponse> responseList = chatClient.prompt()
                    .system(SYSTEM_PROMPT)
                    .user(u -> u.text("Can you summarize the following meeting notes: {meetingNotes}" +
                                    " Give me 3 different summarization in the same format so that I can choose from." +
                                    " Use the format as described in the following example while doing the summarization:" +
                                    " Input: In today’s sales strategy meeting, we reviewed Q3 targets and performance gaps. The team agreed to focus on enterprise clients and strengthen partnerships." +
                                    " A proposal was made to expand into two new regions. Marketing suggested aligning campaigns with sales objectives to improve lead conversion and shorten sales cycles." +
                                    " Output:" +
                                    " Action Items:" +
                                    "* Focus on enterprise clients and partnerships." +
                                    "* Explore expansion into two new regions." +
                                    "* Align marketing campaigns with sales objectives." +
                                    " Decisions:" +
                                    "* Enterprise clients prioritized for Q3." +
                                    "* Marketing and sales to work jointly on lead conversion.")
                            .param("meetingNotes", meetingNotes))
                    .call()
                    .entity(new ParameterizedTypeReference<>() {
                    });
            log.info("Summarization meeting notes structured: {}", responseList.toString());
            return responseList;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }


    @PostMapping("/summarize-with-openai-java-client")
    public String summarizeWithOpenAIJavaClient(@RequestBody String message) {

            String response = openAIService.chat(message);
            log.info("Summarization text: {}", response);
            return response;

    }
}
