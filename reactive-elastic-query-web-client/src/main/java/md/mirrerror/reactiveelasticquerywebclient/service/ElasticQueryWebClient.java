package md.mirrerror.reactiveelasticquerywebclient.service;

import md.mirrerror.elasticquerywebclient.common.model.ElasticQueryWebClientRequestModel;
import md.mirrerror.elasticquerywebclient.common.model.ElasticQueryWebClientResponseModel;
import reactor.core.publisher.Flux;

public interface ElasticQueryWebClient {

    Flux<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel request);

}
