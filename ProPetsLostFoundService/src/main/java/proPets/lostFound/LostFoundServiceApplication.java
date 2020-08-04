package proPets.lostFound;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync

public class LostFoundServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(LostFoundServiceApplication.class, args);
	}
}
