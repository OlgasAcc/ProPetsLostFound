package proPets.lostFound.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import proPets.lostFound.model.post.Post;

@Configuration
@RefreshScope
public class LostFoundConfiguration {

	Map<String, Post> posts = new ConcurrentHashMap<>();

	@Bean(name="processExecutor")
    public TaskExecutor workExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix("Async-");
        threadPoolTaskExecutor.setCorePoolSize(10);
        threadPoolTaskExecutor.setMaxPoolSize(20);
        threadPoolTaskExecutor.setQueueCapacity(600);
        threadPoolTaskExecutor.afterPropertiesSet();
        return threadPoolTaskExecutor;
    }
	
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
	
	@Value("${base.search.url}")
	String baseSearchUrl;

	@RefreshScope
	public String getBaseSearchUrl() {
		return baseSearchUrl;
	}

}