package md.mirrerror.kafka.producer.service.impl;

import jakarta.annotation.PreDestroy;
import md.mirrerror.kafka.avro.model.TwitterAvroModel;
import md.mirrerror.kafka.producer.service.KafkaProducer;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class TwitterKafkaProducer implements KafkaProducer<Long, TwitterAvroModel> {

    private static final Logger log = LoggerFactory.getLogger(TwitterKafkaProducer.class);

    private KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate;

    public TwitterKafkaProducer(KafkaTemplate<Long, TwitterAvroModel> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void send(String topicName, Long key, TwitterAvroModel message) {
        log.info("Sending message='{}' to topic='{}'", message, topicName);
        CompletableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture =
                kafkaTemplate.send(topicName, key, message);
        addKafkaResultCallback(topicName, kafkaResultFuture);
    }

    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
           log.info("Closing Kafka producer...");
           kafkaTemplate.destroy();
        }
    }

    private void addKafkaResultCallback(String topicName,
                                        CompletableFuture<SendResult<Long, TwitterAvroModel>> kafkaResultFuture) {
        kafkaResultFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                log.error("Error while sending message='{}' to topic='{}'", result, topicName, throwable);
                return;
            }

            RecordMetadata recordMetadata = result.getRecordMetadata();
            log.debug("Received new metadata. Topic: {}; partition: {}; offset: {}; timestamp: {}, at time: {}",
                    recordMetadata.topic(),
                    recordMetadata.partition(),
                    recordMetadata.offset(),
                    recordMetadata.timestamp(),
                    System.nanoTime());
        });
    }

}
