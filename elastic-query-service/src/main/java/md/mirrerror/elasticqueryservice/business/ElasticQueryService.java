package md.mirrerror.elasticqueryservice.business;

import md.mirrerror.elasticqueryservice.model.ElasticQueryServiceRequestModel;
import md.mirrerror.elasticqueryservice.model.ElasticQueryServiceResponseModel;

import java.util.List;

public interface ElasticQueryService {

    ElasticQueryServiceResponseModel getDocumentById(String id);
    List<ElasticQueryServiceResponseModel> getDocumentsByText(String text);
    List<ElasticQueryServiceResponseModel> getAllDocuments();

}
