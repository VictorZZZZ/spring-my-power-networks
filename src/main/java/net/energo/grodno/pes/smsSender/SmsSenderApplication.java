package net.energo.grodno.pes.smsSender;

import net.energo.grodno.pes.smsSender.storage.StorageProperties;
import net.energo.grodno.pes.smsSender.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
@EnableCaching
public class SmsSenderApplication {

	public static void main(String[] args) {

		SpringApplication.run(SmsSenderApplication.class, args);
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			//storageService.deleteAll();
			storageService.init();
		};
	}

	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


}
