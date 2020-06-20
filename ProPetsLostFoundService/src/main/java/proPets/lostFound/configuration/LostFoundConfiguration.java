package proPets.lostFound.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.web.client.RestTemplate;

import proPets.lostFound.model.Post;

@Configuration
@ManagedResource
public class LostFoundConfiguration {

	Map<String, Post> posts = new ConcurrentHashMap<>();
	
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Value("${post.quantity}")
	int quantity;
	
	public int getQuantity() {
		return quantity;
	}
}