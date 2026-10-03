package md.mirrerror.aigeneratedtweetstokafkaservice.service.springai;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.aigeneratedtweetstokafkaservice.exception.AiGeneratedTweetsToKafkaException;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.AiService;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.springai.model.TweetResponse;
import md.mirrerror.config.AiGeneratedTweetsToKafkaServiceConfigData;
import org.springframework.ai.chat.client.ChatClient;
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
@ConditionalOnProperty(name = "ai-generated-tweets-to-kafka-service.ai-service", havingValue = "SpringAI-OpenAI")
public class SpringAiOpenAiService implements AiService {

    private final ChatClient chatClient;
    private final AiGeneratedTweetsToKafkaServiceConfigData configData;

    @Value("classpath:/templates/tweet-prompt.st")
    private Resource tweetPrompt;

    public SpringAiOpenAiService(@Qualifier("openAiChatClient") ChatClient chatClient,
                                 AiGeneratedTweetsToKafkaServiceConfigData configData) {
        this.chatClient = chatClient;
        this.configData = configData;
    }

    @Override
    public String generateTweet() throws AiGeneratedTweetsToKafkaException {
        log.info("Generating a tweet using SpringAiOpenAiService");

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

        String modelResult = chatClient.prompt(prompt)
                .call()
                .content();

        log.info("Model result: {}", modelResult);

        return modelResult;
    }

}
