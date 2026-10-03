package md.mirrerror.aigeneratedtweetstokafkaservice.service.openai;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.aigeneratedtweetstokafkaservice.exception.AiGeneratedTweetsToKafkaException;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.AiService;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.openai.model.OpenAiRequest;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.openai.model.OpenAiResponse;
import md.mirrerror.config.AiGeneratedTweetsToKafkaServiceConfigData;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpHeaders;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@ConditionalOnProperty(name = "ai-generated-tweets-to-kafka-service.ai-service", havingValue = "OpenAI")
public class OpenAiService implements AiService {

    private final AiGeneratedTweetsToKafkaServiceConfigData configData;
    private final ObjectMapper objectMapper;

    public OpenAiService(AiGeneratedTweetsToKafkaServiceConfigData configData,
                         ObjectMapper objectMapper) {
        this.configData = configData;
        this.objectMapper = objectMapper;
    }

    @Override
    public String generateTweet() throws AiGeneratedTweetsToKafkaException {
        log.info("Generating a tweet using OpenAiService");

        String prompt = configData.getPrompt().replace(configData.getKeywordsPlaceholder(),
                String.join(",", configData.getStreamingDataKeywords()));

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = getRequest(prompt);
            String response = httpClient.execute(request,
                    resp -> EntityUtils.toString(resp.getEntity()));
            return parseResponse(response);
        } catch (IOException e) {
            throw new AiGeneratedTweetsToKafkaException("Failed to generate tweet from OpenAI", e);
        }
    }

    private HttpPost getRequest(String prompt) {
        HttpPost request = new HttpPost(configData.getOpenAi().getUrl());
        request.addHeader(HttpHeaders.CONTENT_TYPE, configData.getOpenAi().getContentType());
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + configData.getOpenAi().getApiKey());
        OpenAiRequest openAiRequest = OpenAiRequest.builder()
                .model(configData.getOpenAi().getModel())
                .maxCompletionTokens(configData.getOpenAi().getMaxCompletionTokens())
                .temperature(configData.getOpenAi().getTemperature())
                .messages(configData.getOpenAi().getMessages().stream().map(message ->
                        OpenAiRequest.Message.builder()
                                .role(message.getRole())
                                .content(List.of(OpenAiRequest.Content.builder()
                                                .type(message.getContent().getFirst().getType())
                                                .text(prompt)
                                        .build()))
                                .build()
                ).toList())
                .build();

        request.setEntity(new StringEntity(objectMapper.writeValueAsString(openAiRequest)));

        return request;
    }

    private String parseResponse(String response) {
        OpenAiResponse openAiResponse = objectMapper.readValue(response, OpenAiResponse.class);
        return openAiResponse.getChoices().getFirst().getMessage().getContent();
    }

}
