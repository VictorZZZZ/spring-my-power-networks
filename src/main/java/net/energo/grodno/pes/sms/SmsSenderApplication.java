package net.energo.grodno.pes.sms;

import lombok.AllArgsConstructor;
import net.energo.grodno.pes.sms.services.schedule.SchedulerService;
import net.energo.grodno.pes.sms.storage.StorageProperties;
import net.energo.grodno.pes.sms.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableCaching
@EnableScheduling
@AllArgsConstructor
public class SmsSenderApplication {
    private final SchedulerService schedulerService;

    public static void main(String[] args) {

        SpringApplication.run(SmsSenderApplication.class, args);
    }

    @Bean
    CommandLineRunner init(StorageService storageService) {
        return (args) -> {
            storageService.init();
        };
    }
}
