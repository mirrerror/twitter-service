package md.mirrerror.aigeneratedtweetstokafkaservice;

import lombok.extern.slf4j.Slf4j;
import md.mirrerror.aigeneratedtweetstokafkaservice.init.StreamInitializer;
import md.mirrerror.aigeneratedtweetstokafkaservice.runner.AiStreamRunner;
import md.mirrerror.config.AiGeneratedTweetsToKafkaServiceConfigData;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Slf4j
@EnableScheduling
@ComponentScan(basePackages = "md.mirrerror")
@SpringBootApplication
public class AiGeneratedTweetsToKafkaServiceApplication implements CommandLineRunner {

    private final AiGeneratedTweetsToKafkaServiceConfigData configData;
    private final StreamInitializer streamInitializer;
    private final AiStreamRunner aiStreamRunner;
    private final TaskScheduler taskScheduler;

    public AiGeneratedTweetsToKafkaServiceApplication(AiGeneratedTweetsToKafkaServiceConfigData configData,
                                                      StreamInitializer streamInitializer,
                                                      AiStreamRunner aiStreamRunner,
                                                      TaskScheduler taskScheduler) {
        this.configData = configData;
        this.streamInitializer = streamInitializer;
        this.aiStreamRunner = aiStreamRunner;
        this.taskScheduler = taskScheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(AiGeneratedTweetsToKafkaServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Application is starting...");
        boolean initResult = streamInitializer.init();
        if (initResult) {
            log.info("Starting AI Stream Runner with fixed rate of {} seconds.", configData.getSchedulerDurationSec());
            taskScheduler.scheduleAtFixedRate(aiStreamRunner,
                    Duration.of(configData.getSchedulerDurationSec(), ChronoUnit.SECONDS));
        } else {
            log.info("Stream initialization failed to initialize the streams. Not starting the AI Stream Runner.");
        }
    }

}