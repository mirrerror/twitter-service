package md.mirrerror.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "ai-generated-tweets-to-kafka-service")
public class AiGeneratedTweetsToKafkaServiceConfigData {

    private List<String> streamingDataKeywords;
    private Long schedulerDurationSec;
    private String prompt;
    private String keywordsPlaceholder;
    private OpenAi openAi;
    private GoogleGenAi googleGenAi;

    @Data
    public static class OpenAi {
        private String url;
        private String apiKey;
        private String contentType;
        private String model;
        private Integer maxCompletionTokens;
        private Double temperature;
        private List<Message> messages;
    }

    @Data
    public static class Message {
        private String role;
        private List<Content> content;
    }

    @Data
    public static class Content {
        private String type;
        private String text;
    }

    @Data
    public static class GoogleGenAi {
        private String projectId;
        private String location;
        private String modelName;
        private Integer maxOutputTokens;
        private Float temperature;
        private Integer candidateCount;
    }

}
