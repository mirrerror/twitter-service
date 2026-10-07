package md.mirrerror.reactiveelasticqueryservice.business;

import md.mirrerror.elasticmodel.index.IndexModel;
import reactor.core.publisher.Flux;

public interface ReactiveElasticQueryClient<T extends IndexModel> {

    Flux<T> findIndexModelByText(String text);

}
