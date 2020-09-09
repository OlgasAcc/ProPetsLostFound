package proPets.lostFound.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import proPets.lostFound.dao.mongoDB.LostFoundMongoRepository;

@Configuration
@PropertySource({ "classpath:application.properties" })
@EnableMongoRepositories(basePackageClasses = LostFoundMongoRepository.class)

public class MongoDBRepoConfiguration {
	/*
	 * final MongoProperties props = new MongoProperties();
	 * 
	 * @Autowired private Environment env;
	 * 
	 * @Primary
	 * 
	 * @ConfigurationProperties(prefix = "spring.data.mongodb")
	 * 
	 * @Bean public MongoDbFactory mongoDbFactory() { return new
	 * SimpleMongoDbFactory(new
	 * MongoClientURI(env.getProperty("spring.data.mongodb.uri"))); }
	 * 
	 * @Bean public MongoTemplate mongoTemplate() { MongoTemplate mongoTemplate =
	 * new MongoTemplate(mongoDbFactory()); return mongoTemplate;
	 * 
	 * }
	 */
}