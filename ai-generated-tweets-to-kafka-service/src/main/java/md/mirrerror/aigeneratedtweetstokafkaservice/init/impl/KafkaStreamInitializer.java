package md.mirrerror.aigeneratedtweetstokafkaservice.init.impl;

import md.mirrerror.aigeneratedtweetstokafkaservice.init.StreamInitializer;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamInitializer implements StreamInitializer {

    @Override
    public boolean init() {
        return true;
    }

}
