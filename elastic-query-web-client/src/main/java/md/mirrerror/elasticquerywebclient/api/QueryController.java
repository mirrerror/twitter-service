package md.mirrerror.elasticquerywebclient.api;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import md.mirrerror.elasticquerywebclient.common.model.ElasticQueryWebClientRequestModel;
import md.mirrerror.elasticquerywebclient.common.model.ElasticQueryWebClientResponseModel;
import md.mirrerror.elasticquerywebclient.service.ElasticQueryWebClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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

    @GetMapping("/error")
    public String error() {
        return "error";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());
        return "home";
    }

    @PostMapping("/query-by-text")
    public String queryByText(
            @Valid ElasticQueryWebClientRequestModel request,
            Model model
    ) {
        log.info("Querying with text: {}", request.getText());

        List<ElasticQueryWebClientResponseModel> responseModels = elasticQueryWebClient.getDataByText(request);

        model.addAttribute("elasticQueryWebClientResponseModels", responseModels);
        model.addAttribute("searchText", request.getText());
        model.addAttribute("elasticQueryWebClientRequestModel",
                ElasticQueryWebClientRequestModel.builder().build());

        return "home";
    }

}
