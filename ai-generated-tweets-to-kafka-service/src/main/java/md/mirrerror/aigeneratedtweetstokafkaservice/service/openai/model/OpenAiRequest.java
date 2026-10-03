package md.mirrerror.aigeneratedtweetstokafkaservice.service.openai.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OpenAiRequest {

    private String model;
    private List<Message> messages;
    private int maxCompletionTokens;
    private double temperature;

    @Data
    @Builder
    public static class Message {
        private String role;
        private List<Content> content;
    }

    @Data
    @Builder
    public static class Content {
        private String type;
        private String text;
    }

}
