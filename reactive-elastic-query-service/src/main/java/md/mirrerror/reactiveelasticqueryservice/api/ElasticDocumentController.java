package md.mirrerror.reactiveelasticqueryservice.api;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import md.mirrerror.elasticqueryservice.common.model.ElasticQueryServiceRequestModel;
import md.mirrerror.elasticqueryservice.common.model.ElasticQueryServiceResponseModel;
import md.mirrerror.reactiveelasticqueryservice.business.ElasticQueryService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/documents")
public class ElasticDocumentController {

    private final ElasticQueryService elasticQueryService;

    public ElasticDocumentController(ElasticQueryService elasticQueryService) {
        this.elasticQueryService = elasticQueryService;
    }

    @PostMapping(
            value = "/get-doc-by-text",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public Flux<ElasticQueryServiceResponseModel> getDocumentByText(
            @RequestBody @Valid ElasticQueryServiceRequestModel requestModel
    ) {
        Flux<ElasticQueryServiceResponseModel> response =
                elasticQueryService.getDocumentByText(requestModel.getText());
        response = response.log();

        log.info("Returning from query reactive service for text: {}", requestModel.getText());

        return response;
    }

}
