package md.mirrerror.reactiveelasticqueryservice.business;

import md.mirrerror.elasticqueryservice.common.model.ElasticQueryServiceResponseModel;
import reactor.core.publisher.Flux;

public interface ElasticQueryService {

    Flux<ElasticQueryServiceResponseModel> getDocumentByText(String text);

}
