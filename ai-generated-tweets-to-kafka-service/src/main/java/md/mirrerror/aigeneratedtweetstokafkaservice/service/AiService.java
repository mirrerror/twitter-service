package md.mirrerror.aigeneratedtweetstokafkaservice.service;

import md.mirrerror.aigeneratedtweetstokafkaservice.exception.AiGeneratedTweetsToKafkaException;

public interface AiService {

    String generateTweet() throws AiGeneratedTweetsToKafkaException;

}
