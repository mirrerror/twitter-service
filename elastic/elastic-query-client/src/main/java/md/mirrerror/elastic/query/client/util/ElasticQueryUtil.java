package md.mirrerror.elastic.query.client.util;

import md.mirrerror.elasticmodel.index.IndexModel;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ElasticQueryUtil<T extends IndexModel> {

    public Query getSearchQueryById(String id) {
        return NativeQuery.builder()
                .withIds(Collections.singleton(id))
                .build();
    }

    public Query getSearchQueryByFieldText(String field, String text) {
        return NativeQuery.builder()
                .withQuery(q -> q.bool(b ->
                        b.must(m -> m.match(mt -> mt.field(field).query(text)))))
                .build();
    }

    public Query getSearchQueryForAll() {
        return NativeQuery.builder()
                .withQuery(q -> q.bool(b ->
                        b.must(m -> m.matchAll(ma -> ma))))
                .build();
    }

}
