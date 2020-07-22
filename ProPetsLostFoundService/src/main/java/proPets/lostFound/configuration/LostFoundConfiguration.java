package proPets.lostFound.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import proPets.lostFound.model.Post;

@Configuration
@RefreshScope
public class LostFoundConfiguration {

	Map<String, Post> posts = new ConcurrentHashMap<>();

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

	@Value("${post.quantity}")
	int quantity;

	@RefreshScope
	public int getQuantity() {
		return quantity;
	}

	@Value("${base.jwt.url}")
	String baseJWTUrl;

	@RefreshScope
	public String getBaseJWTUrl() {
		return baseJWTUrl;
	}
	
	@Value("${base.search.url}")
	String baseSearchUrl;

	@RefreshScope
	public String getBaseSearchUrl() {
		return baseSearchUrl;
	}

}