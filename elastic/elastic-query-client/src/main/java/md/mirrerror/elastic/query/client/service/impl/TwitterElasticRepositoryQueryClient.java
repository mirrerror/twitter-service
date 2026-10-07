package md.mirrerror.elastic.query.client.service.impl;

import md.mirrerror.common.util.CollectionsUtil;
import md.mirrerror.elastic.query.client.exception.ElasticQueryClientException;
import md.mirrerror.elastic.query.client.repository.TwitterElasticsearchQueryRepository;
import md.mirrerror.elastic.query.client.service.ElasticQueryClient;
import md.mirrerror.elasticmodel.index.impl.TwitterIndexModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Primary
@Service
public class TwitterElasticRepositoryQueryClient implements ElasticQueryClient<TwitterIndexModel> {

    private static final Logger log = LoggerFactory.getLogger(TwitterElasticRepositoryQueryClient.class);

    private final TwitterElasticsearchQueryRepository twitterElasticsearchQueryRepository;

    public TwitterElasticRepositoryQueryClient(TwitterElasticsearchQueryRepository twitterElasticsearchQueryRepository) {
        this.twitterElasticsearchQueryRepository = twitterElasticsearchQueryRepository;
    }

    @Override
    public TwitterIndexModel getIndexModelById(String id) {
        Optional<TwitterIndexModel> searchResult = twitterElasticsearchQueryRepository.findById(id);

        log.info("Found document at Elasticsearch with id {}", searchResult.orElseThrow(() ->
                new ElasticQueryClientException("No document found at Elasticsearch with id " + id)
        ).getId());

        return searchResult.get();
    }

    @Override
    public List<TwitterIndexModel> getIndexModelByText(String text) {
        List<TwitterIndexModel> searchResult = twitterElasticsearchQueryRepository.findByText(text);

        log.info("{} of documents with text {} retrieved successfully", searchResult.size(), text);

        return searchResult;
    }

    @Override
    public List<TwitterIndexModel> getAllIndexModels() {
        List<TwitterIndexModel> searchResult = CollectionsUtil.getInstance()
                .getListFromIterable(twitterElasticsearchQueryRepository.findAll());

        log.info("{} of documents retrieved successfully", searchResult.size());

        return searchResult;
    }

}
