package md.mirrerror.reactiveelasticqueryservice.business.impl;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.elasticmodel.index.impl.TwitterIndexModel;
import md.mirrerror.elasticqueryservice.common.model.ElasticQueryServiceResponseModel;
import md.mirrerror.elasticqueryservice.common.transformer.ElasticToResponseModelTransformer;
import md.mirrerror.reactiveelasticqueryservice.business.ElasticQueryService;
import md.mirrerror.reactiveelasticqueryservice.business.ReactiveElasticQueryClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient;
    private final ElasticToResponseModelTransformer elasticToResponseModelTransformer;

    public TwitterElasticQueryService(ReactiveElasticQueryClient<TwitterIndexModel> reactiveElasticQueryClient,
                                      ElasticToResponseModelTransformer elasticToResponseModelTransformer) {
        this.reactiveElasticQueryClient = reactiveElasticQueryClient;
        this.elasticToResponseModelTransformer = elasticToResponseModelTransformer;
    }

    @Override
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText(String text) {
        log.info("Reactive querying Elasticsearch by text: {}", text);
        return reactiveElasticQueryClient
                .findIndexModelByText(text)
                .map(elasticToResponseModelTransformer::getResponseModel);
    }

}
