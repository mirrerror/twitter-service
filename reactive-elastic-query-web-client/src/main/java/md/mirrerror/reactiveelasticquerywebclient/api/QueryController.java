package md.mirrerror.reactiveelasticquerywebclient.api;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import md.mirrerror.elasticquerywebclient.common.model.ElasticQueryWebClientRequestModel;
import md.mirrerror.elasticquerywebclient.common.model.ElasticQueryWebClientResponseModel;
import md.mirrerror.reactiveelasticquerywebclient.service.ElasticQueryWebClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring6.context.webflux.IReactiveDataDriverContextVariable;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

@Slf4j
@Controller
public class QueryController {

    private final ElasticQueryWebClient elasticQueryWebClient;

    public QueryController(ElasticQueryWebClient elasticQueryWebClient) {
        this.elasticQueryWebClient = elasticQueryWebClient;
    }

    @GetMapping("")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @PostMapping(value = "query-by-text")
    public String queryByText(
            @Valid ElasticQueryWebClientRequestModel requestModel,
            Model model
    ) {
        Flux<ElasticQueryWebClientResponseModel> responseModel =
                elasticQueryWebClient.getDataByText(requestModel);
        responseModel = responseModel.log();

        IReactiveDataDriverContextVariable reactiveData =
                new ReactiveDataDriverContextVariable(responseModel, 1);
        model.addAttribute("elasticQueryWebClientResponseModels", reactiveData);
        model.addAttribute("searchText", requestModel.getText());
        model.addAttribute("elasticQueryWebClientResponseModel",
                ElasticQueryWebClientResponseModel.builder().build());

        log.info("Returning from reactive client controller for text {}", requestModel.getText());

        return "home";
    }

}
