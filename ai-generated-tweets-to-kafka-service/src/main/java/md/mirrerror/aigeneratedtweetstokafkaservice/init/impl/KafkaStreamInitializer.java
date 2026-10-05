package md.mirrerror.aigeneratedtweetstokafkaservice.init.impl;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.aigeneratedtweetstokafkaservice.init.StreamInitializer;
import md.mirrerror.config.KafkaConfigData;
import md.mirrerror.kafka.admin.client.KafkaAdminClient;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaStreamInitializer implements StreamInitializer {

    private final KafkaConfigData kafkaConfigData;
    private final KafkaAdminClient kafkaAdminClient;

    public KafkaStreamInitializer(KafkaConfigData kafkaConfigData,
                                  KafkaAdminClient kafkaAdminClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.kafkaAdminClient = kafkaAdminClient;
    }

    @Override
    public boolean init() {
        try {
            kafkaAdminClient.createTopics();
            kafkaAdminClient.checkSchemaRegistry();
            log.info("Topics {} are ready for operations.", kafkaConfigData.getTopicNamesToCreate().toArray());

            return true;
        } catch (Throwable t) {
            log.error("Error while initializing Kafka Streams", t);
        }

        return false;
    }

}
