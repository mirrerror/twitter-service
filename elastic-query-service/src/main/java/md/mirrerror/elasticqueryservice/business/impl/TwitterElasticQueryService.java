package md.mirrerror.elasticqueryservice.business.impl;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.elastic.query.client.service.ElasticQueryClient;
import md.mirrerror.elasticmodel.index.impl.TwitterIndexModel;
import md.mirrerror.elasticqueryservice.business.ElasticQueryService;
import md.mirrerror.elasticqueryservice.common.model.ElasticQueryServiceResponseModel;
import md.mirrerror.elasticqueryservice.model.assembler.ElasticQueryServiceResponseModelAssembler;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TwitterElasticQueryService implements ElasticQueryService {

    private final ElasticQueryServiceResponseModelAssembler elasticQueryServiceResponseModelAssembler;
    private final ElasticQueryClient<TwitterIndexModel> elasticQueryClient;

    public TwitterElasticQueryService(ElasticQueryServiceResponseModelAssembler elasticQueryServiceResponseModelAssembler,
                                      ElasticQueryClient<TwitterIndexModel> elasticQueryClient) {
        this.elasticQueryServiceResponseModelAssembler = elasticQueryServiceResponseModelAssembler;
        this.elasticQueryClient = elasticQueryClient;
    }

    @Override
    public ElasticQueryServiceResponseModel getDocumentById(String id) {
        log.info("Querying Elasticsearch by id: {}", id);
        return elasticQueryServiceResponseModelAssembler.toModel(
                elasticQueryClient.getIndexModelById(id)
        );
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getDocumentsByText(String text) {
        log.info("Querying Elasticsearch by text: {}", text);
        return elasticQueryServiceResponseModelAssembler.toModels(
                elasticQueryClient.getIndexModelByText(text)
        );
    }

    @Override
    public List<ElasticQueryServiceResponseModel> getAllDocuments() {
        log.info("Querying all documents in Elasticsearch");
        return elasticQueryServiceResponseModelAssembler.toModels(
                elasticQueryClient.getAllIndexModels()
        );
    }

}
