package md.mirrerror.aigeneratedtweetstokafkaservice.transformer;

import md.mirrerror.aigeneratedtweetstokafkaservice.service.springai.model.TweetResponse;
import md.mirrerror.kafka.avro.model.TwitterAvroModel;
import org.springframework.stereotype.Component;

@Component
public class TwitterResponseToAvroModelTransformer {

    public TwitterAvroModel getTwitterAvroModelFromTwitterResponse(TweetResponse tweetResponse) {
        return TwitterAvroModel.newBuilder()
                .setId(tweetResponse.id())
                .setUserId(tweetResponse.user().id())
                .setText(tweetResponse.text())
                .setCreatedAt(tweetResponse.createdAt().toEpochSecond())
                .build();
    }

}
