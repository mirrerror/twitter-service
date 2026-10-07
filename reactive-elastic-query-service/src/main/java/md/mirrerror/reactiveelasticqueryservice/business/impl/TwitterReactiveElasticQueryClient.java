package md.mirrerror.reactiveelasticqueryservice.business.impl;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.config.ElasticQueryServiceConfigData;
import md.mirrerror.elasticmodel.index.impl.TwitterIndexModel;
import md.mirrerror.reactiveelasticqueryservice.business.ReactiveElasticQueryClient;
import md.mirrerror.reactiveelasticqueryservice.repository.ElasticQueryRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
@Service
public class TwitterReactiveElasticQueryClient implements ReactiveElasticQueryClient<TwitterIndexModel> {

    private final ElasticQueryRepository elasticQueryRepository;
    private final ElasticQueryServiceConfigData elasticQueryServiceConfigData;

    public TwitterReactiveElasticQueryClient(ElasticQueryRepository elasticQueryRepository,
                                             ElasticQueryServiceConfigData elasticQueryServiceConfigData) {
        this.elasticQueryRepository = elasticQueryRepository;
        this.elasticQueryServiceConfigData = elasticQueryServiceConfigData;
    }

    @Override
    public Flux<TwitterIndexModel> findIndexModelByText(String text) {
        log.info("Getting data from Elasticsearch by text: {}", text);
        return elasticQueryRepository.findByText(text)
                .delayElements(Duration.ofMillis(
                        elasticQueryServiceConfigData.getBackPressureDelayMs()
                ));
    }

}
