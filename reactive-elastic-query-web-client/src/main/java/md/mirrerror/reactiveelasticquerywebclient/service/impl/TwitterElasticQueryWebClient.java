package md.mirrerror.reactiveelasticquerywebclient.service.impl;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.config.ElasticQueryWebClientConfigData;
import md.mirrerror.elasticquerywebclient.common.exception.ElasticQueryWebClientException;
import md.mirrerror.elasticquerywebclient.common.model.ElasticQueryWebClientRequestModel;
import md.mirrerror.elasticquerywebclient.common.model.ElasticQueryWebClientResponseModel;
import md.mirrerror.reactiveelasticquerywebclient.service.ElasticQueryWebClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class TwitterElasticQueryWebClient implements ElasticQueryWebClient {

    private final WebClient webClient;
    private final ElasticQueryWebClientConfigData elasticQueryWebClientConfigData;

    public TwitterElasticQueryWebClient(@Qualifier("webClient") WebClient webClient,
                                        ElasticQueryWebClientConfigData elasticQueryWebClientConfigData) {
        this.webClient = webClient;
        this.elasticQueryWebClientConfigData = elasticQueryWebClientConfigData;
    }

    @Override
    public Flux<ElasticQueryWebClientResponseModel> getDataByText(ElasticQueryWebClientRequestModel requestModel) {
        log.info("Querying by text {}", requestModel.getText());
        return getWebClient(requestModel)
                .bodyToFlux(ElasticQueryWebClientResponseModel.class);
    }

    private WebClient.ResponseSpec getWebClient(ElasticQueryWebClientRequestModel requestModel) {
        return webClient
                .method(HttpMethod.valueOf(elasticQueryWebClientConfigData.getQueryByText().getMethod()))
                .uri(elasticQueryWebClientConfigData.getQueryByText().getUri())
                .body(BodyInserters.fromPublisher(Mono.just(requestModel), createParameterizedTypeReference()))
                .retrieve()
                .onStatus(
                        httpStatus -> httpStatus.equals(HttpStatus.UNAUTHORIZED),
                        clientResponse -> Mono.just(new BadCredentialsException("Not authenticated")))
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.just(new ElasticQueryWebClientException(
                                HttpStatus.valueOf(clientResponse.statusCode().value()).getReasonPhrase())))
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.just(new Exception(
                                HttpStatus.valueOf(clientResponse.statusCode().value()).getReasonPhrase())));
    }

    private <T> ParameterizedTypeReference<T> createParameterizedTypeReference() {
        return new ParameterizedTypeReference<>() {
        };
    }

}
