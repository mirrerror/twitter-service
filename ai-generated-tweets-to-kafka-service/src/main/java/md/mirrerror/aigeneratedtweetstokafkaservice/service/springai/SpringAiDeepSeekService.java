package md.mirrerror.aigeneratedtweetstokafkaservice.service.springai;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.aigeneratedtweetstokafkaservice.config.AiGeneratedTweetsToKafkaServiceConfigData;
import md.mirrerror.aigeneratedtweetstokafkaservice.exception.AiGeneratedTweetsToKafkaException;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.AiService;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.springai.model.TweetResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@ConditionalOnProperty(name = "ai-generated-tweets-to-kafka-service.ai-service", havingValue = "SpringAI-DeepSeek")
public class SpringAiDeepSeekService implements AiService {

    public static final String DEEP_SEEK_THINK_REGEX = "(?s)<think>.*?</think>";

    private final ChatClient chatClient;
    private final AiGeneratedTweetsToKafkaServiceConfigData configData;

    @Value("classpath:/templates/tweet-prompt.st")
    private Resource tweetPrompt;

    public SpringAiDeepSeekService(@Qualifier("ollamaChatClient") ChatClient chatClient,
                                   AiGeneratedTweetsToKafkaServiceConfigData configData) {
        this.chatClient = chatClient;
        this.configData = configData;
    }

    @Override
    public String generateTweet() throws AiGeneratedTweetsToKafkaException {
        log.info("Generating a tweet using SpringAiDeepSeekService");

        BeanOutputConverter<TweetResponse> converter = new BeanOutputConverter<>(TweetResponse.class);

        log.info("Converter format: {}", converter.getFormat());

        PromptTemplate promptTemplate = new PromptTemplate(tweetPrompt);
        Prompt prompt = promptTemplate.create(Map.of(
                configData.getKeywordsPlaceholder()
                        .replace("{", "")
                        .replace("}", ""),
                String.join(",", configData.getStreamingDataKeywords()),
                "format",
                converter.getFormat()
        ));

        ChatClientResponse chatClientResponse = chatClient.prompt(prompt)
                .call()
                .chatClientResponse();

        String modelResult = chatClientResponse
                .chatResponse()
                .getResult()
                .getOutput()
                .getText();

        log.info("Model result: {} with model {}", modelResult,
                chatClientResponse.chatResponse().getMetadata().getModel());

        return modelResult.replaceAll(DEEP_SEEK_THINK_REGEX, "").trim();
    }

}
