package md.mirrerror.aigeneratedtweetstokafkaservice.service;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessage;
import lombok.extern.slf4j.Slf4j;
import md.mirrerror.aigeneratedtweetstokafkaservice.config.AiGeneratedTweetsToKafkaServiceConfigData;
import md.mirrerror.aigeneratedtweetstokafkaservice.exception.AiGeneratedTweetsToKafkaException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@ConditionalOnProperty(name = "ai-generated-tweets-to-kafka-service.ai-service", havingValue = "OpenAI-JavaClient")
public class OpenAiJavaClientService implements AiService {

    private final AiGeneratedTweetsToKafkaServiceConfigData configData;

    public OpenAiJavaClientService(AiGeneratedTweetsToKafkaServiceConfigData configData) {
        this.configData = configData;
    }

    @Override
    public String generateTweet() throws AiGeneratedTweetsToKafkaException {
        log.info("Generating a tweet using OpenAiJavaClientService");

        String prompt = configData.getPrompt().replace(configData.getKeywordsPlaceholder(),
                String.join(",", configData.getStreamingDataKeywords()));

        return getPromptResponse(prompt);
    }

    private String getPromptResponse(String prompt) {
        OpenAIClient client = OpenAIOkHttpClient.fromEnv();

        ChatCompletionCreateParams.Builder createParams = ChatCompletionCreateParams.builder()
                .model(ChatModel.of(configData.getOpenAi().getModel()))
                .addDeveloperMessage("You're helping me to create a tweet content based on the given format and keywords.")
                .maxCompletionTokens(configData.getOpenAi().getMaxCompletionTokens())
                .temperature(configData.getOpenAi().getTemperature())
                .addUserMessage(prompt);

        List<ChatCompletionMessage> messages = client.chat()
                .completions()
                .create(createParams.build())
                .choices()
                .stream()
                .map(ChatCompletion.Choice::message)
                .toList();

        return messages.getFirst().content().get();
    }

}
