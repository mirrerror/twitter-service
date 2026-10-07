package md.mirrerror.elastic.index.client.service;

import md.mirrerror.elasticmodel.index.IndexModel;

import java.util.List;

public interface ElasticIndexClient<T extends IndexModel> {

    List<String> save(List<T> documents);

}
