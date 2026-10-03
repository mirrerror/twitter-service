package md.mirrerror.aigeneratedtweetstokafkaservice.service.googlegenai;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import lombok.extern.slf4j.Slf4j;
import md.mirrerror.aigeneratedtweetstokafkaservice.exception.AiGeneratedTweetsToKafkaException;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.AiService;
import md.mirrerror.config.AiGeneratedTweetsToKafkaServiceConfigData;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;

@Service
@Slf4j
@ConditionalOnProperty(name = "ai-generated-tweets-to-kafka-service.ai-service", havingValue = "Google-GenAI")
public class GoogleGenAiService implements AiService {

    private final AiGeneratedTweetsToKafkaServiceConfigData configData;
    private final Client googleGenAiClient;

    public GoogleGenAiService(AiGeneratedTweetsToKafkaServiceConfigData configData) {
        this.configData = configData;
        this.googleGenAiClient = Client.builder()
                .project(configData.getGoogleGenAi().getProjectId())
                .location(configData.getGoogleGenAi().getLocation())
                .vertexAI(true)
                .build();
    }

    @PreDestroy
    public void close() {
        if (googleGenAiClient != null) {
            googleGenAiClient.close();
        }
    }

    @Override
    public String generateTweet() throws AiGeneratedTweetsToKafkaException {
        log.info("Generating a tweet using GoogleGenAiService");

        String prompt = configData.getPrompt().replace(configData.getKeywordsPlaceholder(),
                String.join(",", configData.getStreamingDataKeywords()));

        return getPromptResponse(prompt);
    }

    private String getPromptResponse(String prompt) {
        GenerateContentConfig config = GenerateContentConfig.builder()
                .maxOutputTokens(configData.getGoogleGenAi().getMaxOutputTokens())
                .temperature(configData.getGoogleGenAi().getTemperature())
                .candidateCount(configData.getGoogleGenAi().getCandidateCount())
                .build();
        String modelName = configData.getGoogleGenAi().getModelName();

        GenerateContentResponse response =
                googleGenAiClient.models.generateContent(modelName, prompt, config);

        return response.text();
    }

}
