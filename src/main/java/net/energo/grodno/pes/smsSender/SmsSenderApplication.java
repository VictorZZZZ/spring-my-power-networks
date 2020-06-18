package net.energo.grodno.pes.smsSender;

import net.energo.grodno.pes.smsSender.storage.StorageProperties;
import net.energo.grodno.pes.smsSender.storage.StorageService;
import net.energo.grodno.pes.smsSender.utils.ShoppingCart;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;




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


}
