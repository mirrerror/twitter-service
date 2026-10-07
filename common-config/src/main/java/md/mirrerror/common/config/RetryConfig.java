package md.mirrerror.common.config;

import md.mirrerror.config.RetryConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;

import java.time.Duration;

@Configuration
public class RetryConfig {

    private final RetryConfigData retryConfigData;

    public RetryConfig(RetryConfigData retryConfigData) {
        this.retryConfigData = retryConfigData;
    }

    @Bean
    public RetryTemplate kafkaRetryTemplate() {
        RetryPolicy retryPolicy = RetryPolicy.builder()
                // maxRetries excludes the initial attempt, maxAttempts includes it
                .maxRetries(retryConfigData.getMaxAttempts() - 1)
                .delay(Duration.ofMillis(retryConfigData.getInitialIntervalMs()))
                .maxDelay(Duration.ofMillis(retryConfigData.getMaxIntervalMs()))
                .multiplier(retryConfigData.getMultiplier())
                .build();

        return new RetryTemplate(retryPolicy);
    }

}
