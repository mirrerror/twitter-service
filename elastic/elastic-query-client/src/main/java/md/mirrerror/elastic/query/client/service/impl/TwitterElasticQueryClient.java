package md.mirrerror.elastic.query.client.service.impl;

import md.mirrerror.config.ElasticConfigData;
import md.mirrerror.config.ElasticQueryConfigData;
import md.mirrerror.elastic.query.client.exception.ElasticQueryClientException;
import md.mirrerror.elastic.query.client.service.ElasticQueryClient;
import md.mirrerror.elastic.query.client.util.ElasticQueryUtil;
import md.mirrerror.elasticmodel.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TwitterElasticQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private static final Logger log = LoggerFactory.getLogger(TwitterElasticQueryClient.class);

    private final ElasticConfigData elasticConfigData;
    private final ElasticQueryConfigData elasticQueryConfigData;
    private final ElasticsearchOperations elasticsearchOperations;
    private final ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil;

    public TwitterElasticQueryClient(ElasticConfigData elasticConfigData,
                                     ElasticQueryConfigData elasticQueryConfigData,
                                     ElasticsearchOperations elasticsearchOperations,
                                     ElasticQueryUtil<TwitterIndexModel> elasticQueryUtil) {
        this.elasticConfigData = elasticConfigData;
        this.elasticQueryConfigData = elasticQueryConfigData;
        this.elasticsearchOperations = elasticsearchOperations;
        this.elasticQueryUtil = elasticQueryUtil;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Query query = elasticQueryUtil.getSearchQueryById(id);
        SearchHit<TwitterIndexModel> searchResult = elasticsearchOperations.searchOne(query, TwitterIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName()));

        if (searchResult == null) {
            log.error("No document found at Elasticsearch with id {}", id);
            throw new ElasticQueryClientException("No document found at Elasticsearch with id " + id);
        }

        log.info("Found document at Elasticsearch with id {}", searchResult.getId());

        return searchResult.getContent();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        Query query = elasticQueryUtil.getSearchQueryByFieldText(elasticQueryConfigData.getTextField(), text);
        return search(query, "{} of documents with text {} retrieved successfully", text);
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        Query query = elasticQueryUtil.getSearchQueryForAll();
        return search(query, "{} of documents retrieved successfully");
    }

    private List<TwitterIndexModel> search(Query query, String logMessage, Object... logParams) {
        SearchHits<TwitterIndexModel> searchResult = elasticsearchOperations.search(
                query,
                TwitterIndexModel.class,
                IndexCoordinates.of(elasticConfigData.getIndexName())
        );

        log.info(logMessage, searchResult.getTotalHits(), logParams);

        return searchResult.get().map(SearchHit::getContent).toList();
    }

}
