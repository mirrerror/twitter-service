package md.mirrerror.reactiveelasticquerywebclient.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import md.mirrerror.config.ElasticQueryWebClientConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {

    private final ElasticQueryWebClientConfigData.WebClient webClientConfig;

    public WebClientConfig(ElasticQueryWebClientConfigData clientConfigData) {
        this.webClientConfig = clientConfigData.getWebClient();
    }

    @Bean("webClient")
    WebClient webClient() {
        return WebClient.builder()
                .baseUrl(webClientConfig.getBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
                .build();
    }

    private HttpClient getHttpClient() {
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, webClientConfig.getConnectTimeoutMs())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(
                            new ReadTimeoutHandler(webClientConfig.getReadTimeoutMs(),
                                    TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(
                            new WriteTimeoutHandler(webClientConfig.getWriteTimeoutMs(),
                                    TimeUnit.MILLISECONDS));
                });
    }

}