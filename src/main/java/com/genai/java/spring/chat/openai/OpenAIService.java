package com.genai.java.spring.chat.openai;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
public class OpenAIService {

    private static final String OPENNAI_API_KEY = "OPENAI_API_KEY"; // Replace
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions"; // Replace
    private static final String OPENAI_MODEL = "gpt-4"; // Replace with the desired model
    private static final String CONTENT_TYPE = "application/json";
    private static final String SYSTEM_PROMPT = "You are a helpful assistant that summarizes any given content. " +
            "Ensure the summary is concise, informative, and captures the key points. " +
            "Use a friendly and approachable tone while maintaining professionalism." +
            "Do not answer anything other than the summarization. If the question is not about summarization," +
            "respond with 'I can only help with summarization tasks.'";

    private final ObjectMapper objectMapper;

    public OpenAIService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String chat(String prompt) throws OpenAIChatException {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            var request = getRequest(prompt);
            var response = httpClient.execute(request, httpResponse -> EntityUtils.toString(httpResponse.getEntity()));
            return parseResponse(response);

        } catch (IOException e) {
            throw new OpenAIChatException("Cloud not call OpenAI API using Java client! ", e);
        }
    }


    private HttpPost getRequest(String prompt) throws JsonProcessingException {
        var request = new HttpPost(OPENAI_API_URL);
        var openAIApiKey = System.getenv(OPENNAI_API_KEY);
        request.addHeader(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE);
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + openAIApiKey);

        Map<String, Object> userMessage = Map.of(
                "role", "user", "content", prompt);
        Map<String, Object> systemMessage = Map.of(
                "role", "system", "content", SYSTEM_PROMPT);

        Map<String, Object> requestBody = Map.of(
                "model", OPENAI_MODEL,
                "messages", List.of(userMessage, systemMessage)
        );

        String requestBodyJson = objectMapper.writeValueAsString(requestBody);
        request.setEntity(new org.apache.hc.core5.http.io.entity.StringEntity(requestBodyJson));
        return request;
    }

    private String parseResponse(String response) {
        Map<String, Object> openAIResponse = null;
        try {
            openAIResponse = objectMapper.readValue(response, Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return openAIResponse.get("choices").toString(); // Adjust this based on the actual response structure
    }
}
