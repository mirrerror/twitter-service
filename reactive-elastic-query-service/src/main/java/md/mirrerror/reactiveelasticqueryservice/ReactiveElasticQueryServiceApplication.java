package md.mirrerror.reactiveelasticqueryservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "md.mirrerror")
public class ReactiveElasticQueryServiceApplication {

    public static void main(String[] args) {
        System.setProperty("org.apache.avro.SERIALIZABLE_PACKAGES", "md.mirrerror.kafka.avro.model");
        SpringApplication.run(ReactiveElasticQueryServiceApplication.class, args);
    }

}
