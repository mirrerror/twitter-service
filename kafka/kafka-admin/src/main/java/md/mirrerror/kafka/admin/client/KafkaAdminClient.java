package md.mirrerror.kafka.admin.client;

import md.mirrerror.config.KafkaConfigData;
import md.mirrerror.config.RetryConfigData;
import md.mirrerror.kafka.admin.exception.KafkaClientException;
import org.apache.kafka.clients.ClientResponse;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.admin.TopicListing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.retry.RetryContext;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class KafkaAdminClient {

    private static final Logger log = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final KafkaConfigData kafkaConfigData;
    private final RetryConfigData retryConfigData;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;
    private final WebClient webClient;

    public KafkaAdminClient(KafkaConfigData kafkaConfigData,
                            RetryConfigData retryConfigData,
                            AdminClient adminClient,
                            RetryTemplate retryTemplate,
                            WebClient webClient) {
        this.kafkaConfigData = kafkaConfigData;
        this.retryConfigData = retryConfigData;
        this.adminClient = adminClient;
        this.retryTemplate = retryTemplate;
        this.webClient = webClient;
    }

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        AtomicInteger attempt = new AtomicInteger();
        try {
            createTopicsResult = retryTemplate.execute(() -> doCreateTopics(attempt.incrementAndGet()));
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max number of retry for creating kafka topic(s)!", t);
        }

        checkTopicsCreated();
    }

    private CreateTopicsResult doCreateTopics(int attempt) {
        List<String> topicNames = kafkaConfigData.getTopicNamesToCreate();

        log.info("Creating {} topics, attempt {}",  topicNames.size(), attempt);

        List<NewTopic> kafkaTopics = topicNames.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigData.getNumOfPartitions(),
                kafkaConfigData.getReplicationFactor()
        )).toList();

        return adminClient.createTopics(kafkaTopics);
    }

    public void checkTopicsCreated() {
        Collection<TopicListing> topics = getTopics();

        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        for (String topic : kafkaConfigData.getTopicNamesToCreate()) {
            while (!isTopicCreated(topics, topic)) {
                checkMaxRetry(retryCount++, maxRetry);
                sleep(sleepTimeMs);
                sleepTimeMs *= multiplier;
                topics = getTopics();
            }
        }
    }

    public void checkSchemaRegistry() {
        int retryCount = 1;
        Integer maxRetry = retryConfigData.getMaxAttempts();
        int multiplier = retryConfigData.getMultiplier().intValue();
        Long sleepTimeMs = retryConfigData.getSleepTimeMs();

        while (!getSchemaRegistryStatus().is2xxSuccessful()) {
            checkMaxRetry(retryCount++, maxRetry);
            sleep(sleepTimeMs);
            sleepTimeMs *= multiplier;
        }
    }

    private HttpStatus getSchemaRegistryStatus() {
        try {
            return webClient
                    .method(HttpMethod.GET)
                    .uri(kafkaConfigData.getSchemaRegistryUrl())
                    .exchangeToMono(response -> Mono.just(HttpStatus.valueOf(response.statusCode().value())))
                    .block();
        } catch (Exception e) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        }
    }

    private void sleep(Long sleepTimeMs) {
        try {
            Thread.sleep(sleepTimeMs);
        } catch (InterruptedException e) {
            throw new KafkaClientException("Error while sleeping to wait for new created topics.", e);
        }
    }

    private void checkMaxRetry(int retryCount, Integer maxRetry) {
        if (retryCount > maxRetry) {
            throw new KafkaClientException("Reached max number of retries for reading Kafka topics.");
        }
    }

    private boolean isTopicCreated(Collection<TopicListing> topics, String topicName) {
        if (topics == null)  {
            return false;
        }

        return topics.stream().anyMatch(topic -> topic.name().equals(topicName));
    }

    private Collection<TopicListing> getTopics() {
        Collection<TopicListing> topics;
        AtomicInteger attempt = new AtomicInteger();
        try {
            topics = retryTemplate.execute(() -> doGetTopics(attempt.incrementAndGet()));
        } catch (Throwable t) {
            throw new KafkaClientException("Reached max number of retries for reading Kafka topics.", t);
        }

        return topics;
    }

    private Collection<TopicListing> doGetTopics(int attempt) throws ExecutionException, InterruptedException {
        log.info("Reading Kafka topic {}, attempt {}",
                kafkaConfigData.getTopicNamesToCreate().toArray(), attempt);

        Collection<TopicListing> topics = adminClient.listTopics().listings().get();
        if (topics != null) {
            topics.forEach(topic -> log.debug("Topic with name {}", topic.name()));
        }

        return topics;
    }

}
