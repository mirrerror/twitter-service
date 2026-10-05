package md.mirrerror.aigeneratedtweetstokafkaservice.runner;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.aigeneratedtweetstokafkaservice.exception.AiGeneratedTweetsToKafkaException;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.AiService;
import md.mirrerror.aigeneratedtweetstokafkaservice.service.springai.model.TweetResponse;
import md.mirrerror.aigeneratedtweetstokafkaservice.transformer.TwitterResponseToAvroModelTransformer;
import md.mirrerror.config.KafkaConfigData;
import md.mirrerror.kafka.avro.model.TwitterAvroModel;
import md.mirrerror.kafka.producer.service.KafkaProducer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Component
public class AiStreamRunner implements Runnable {

    private final AiService aiService;
    private final KafkaConfigData kafkaConfigData;
    private final KafkaProducer<Long, TwitterAvroModel> kafkaProducer;
    private final TwitterResponseToAvroModelTransformer twitterResponseToAvroModelTransformer;
    private final ObjectMapper objectMapper;

    public AiStreamRunner(AiService aiService,
                          KafkaConfigData kafkaConfigData,
                          KafkaProducer<Long, TwitterAvroModel> kafkaProducer,
                          TwitterResponseToAvroModelTransformer twitterResponseToAvroModelTransformer,
                          ObjectMapper objectMapper) {
        this.aiService = aiService;
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaProducer = kafkaProducer;
        this.twitterResponseToAvroModelTransformer = twitterResponseToAvroModelTransformer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run() {
        String generatedTweet = aiService.generateTweet();
        log.info("Generated tweet: {}", generatedTweet);

        TweetResponse tweetResponse;
        try {
            tweetResponse = objectMapper.readValue(generatedTweet, TweetResponse.class);
        } catch (Throwable t) {
            throw new AiGeneratedTweetsToKafkaException("Unable to parse generated tweet", t);
        }

        TwitterAvroModel twitterAvroModel =
                twitterResponseToAvroModelTransformer.getTwitterAvroModelFromTwitterResponse(tweetResponse);
        kafkaProducer.send(kafkaConfigData.getTopicName(), twitterAvroModel.getUserId(), twitterAvroModel);
    }

}
