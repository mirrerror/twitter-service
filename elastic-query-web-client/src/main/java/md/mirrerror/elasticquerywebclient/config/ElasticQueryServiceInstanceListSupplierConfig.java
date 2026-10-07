package md.mirrerror.elasticquerywebclient.config;

import md.mirrerror.config.ElasticQueryWebClientConfigData;
import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;

import java.util.List;

@Primary
@Configuration
public class ElasticQueryServiceInstanceListSupplierConfig implements ServiceInstanceListSupplier {

    private final ElasticQueryWebClientConfigData.WebClient webClientConfig;

    public ElasticQueryServiceInstanceListSupplierConfig(ElasticQueryWebClientConfigData webClientConfigData) {
        this.webClientConfig = webClientConfigData.getWebClient();
    }

    @Override
    public String getServiceId() {
        return webClientConfig.getServiceId();
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        return Flux.just(
                webClientConfig.getInstances().stream()
                        .<ServiceInstance>map(instance ->
                                new DefaultServiceInstance(
                                        instance.getId(),
                                        getServiceId(),
                                        instance.getHost(),
                                        instance.getPort(),
                                        false
                                )).toList()
        );
    }

}
