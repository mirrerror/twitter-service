package md.mirrerror.aigeneratedtweetstokafkaservice.runner;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.AiService;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AiStreamRunner implements Runnable {

    private final AiService aiService;

    public AiStreamRunner(AiService aiService) {
        this.aiService = aiService;
    }

    @Override
    public void run() {
        String generatedTweet = aiService.generateTweet();
        log.info("Generated tweet: {}", generatedTweet);
    }

}
