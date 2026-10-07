package md.mirrerror.elasticquerywebclient.service;

import md.mirrerror.elasticquerywebclient.model.ElasticQueryWebClientRequestModel;
import md.mirrerror.elasticquerywebclient.model.ElasticQueryWebClientResponseModel;

import java.util.List;

public interface ElasticQueryWebClient {

    List<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel);

}
