package md.mirrerror.elasticquerywebclient.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import md.mirrerror.config.ElasticQueryWebClientConfigData;
import md.mirrerror.config.UserConfigData;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
@LoadBalancerClient(name = "elastic-query-service", configuration = ElasticQueryServiceInstanceListSupplierConfig.class)
public class WebClientConfig {

    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;
    private final UserConfigData userConfigData;

    public WebClientConfig(ElasticQueryWebClientConfigData elasticQueryWebClientConfigData,
                           UserConfigData userConfigData) {
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData;
        this.userConfigData = userConfigData;
    }

    @LoadBalanced
    @Bean("webClientBuilder")
    WebClient.Builder webClientBuilder() {
        return WebClient.builder()
                .defaultHeaders(headers ->
                        headers.setBasicAuth(userConfigData.getUsername(), userConfigData.getPassword()))
                .baseUrl(elasticQueryWebClientConfigData.getWebClient().getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
                .codecs(clientCodecConfigurer ->
                        clientCodecConfigurer
                                .defaultCodecs()
                                .maxInMemorySize(elasticQueryWebClientConfigData.getWebClient().getMaxInMemorySize()));
    }

    private HttpClient getHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
                        elasticQueryWebClientConfigData.getWebClient().getConnectTimeoutMs())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(
                            new ReadTimeoutHandler(elasticQueryWebClientConfigData.getWebClient().getReadTimeoutMs(),
                                    TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(
                            new WriteTimeoutHandler(elasticQueryWebClientConfigData.getWebClient().getWriteTimeoutMs(),
                                    TimeUnit.MILLISECONDS));
                });
    }

}