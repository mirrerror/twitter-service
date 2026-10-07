package md.mirrerror.elastic.config;

import md.mirrerror.config.ElasticConfigData;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;

@Configuration
@EnableElasticsearchRepositories(basePackages = "md.mirrerror.elastic")
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    private final ElasticConfigData elasticConfigData;

    public ElasticSearchConfig(ElasticConfigData elasticConfigData) {
        this.elasticConfigData = elasticConfigData;
    }

    @Override
    public ClientConfiguration clientConfiguration() {
        UriComponents serverUri = UriComponentsBuilder.fromUriString(elasticConfigData.getConnectionUrl()).build();
        return ClientConfiguration.builder()
                .connectedTo(serverUri.getHost() + ":" + serverUri.getPort())
                .withConnectTimeout(Duration.ofMillis(elasticConfigData.getConnectionTimeoutMs()))
                .withSocketTimeout(Duration.ofMillis(elasticConfigData.getSocketTimeoutMs()))
                .build();
    }

}