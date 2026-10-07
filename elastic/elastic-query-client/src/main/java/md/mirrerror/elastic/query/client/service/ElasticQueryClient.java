package md.mirrerror.elastic.query.client.service;

import md.mirrerror.elasticmodel.index.IndexModel;

import java.util.List;

public interface ElasticQueryClient<T extends IndexModel> {

    T getIndexModelById(String id);
    List<T> getIndexModelByText(String text);
    List<T> getAllIndexModels();

}
