package proPets.lostFound.configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import proPets.lostFound.dao.LostFoundJPARepository;
import proPets.lostFound.model.AccessCode;

@Configuration
@PropertySource({ "application.properties" })
@EnableJpaRepositories(basePackageClasses = LostFoundJPARepository.class)

public class PostgreSQLRepoConfiguration {

	@Primary
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource postgresqlDataSource() {
		return DataSourceBuilder.create().build();
	}

	@Bean(name = "accessCodeEntityManager")
	public LocalContainerEntityManagerFactoryBean postgresqlEntityManagerFactory(EntityManagerFactoryBuilder builder) {
		return builder.dataSource(postgresqlDataSource()).properties(hibernateProperties()).packages(AccessCode.class)
				.persistenceUnit("userPU").build();
	}

	@Bean(name = "accessCodeTransactionManager")
	public PlatformTransactionManager postgresqlTransactionManager(
			@Qualifier("userEntityManager") EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	private Map<String, ?> hibernateProperties() {

		Resource resource = new ClassPathResource("hibernate.properties");

		try {
			Properties properties = PropertiesLoaderUtils.loadProperties(resource);

			return properties.entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue()));
		} catch (IOException e) {
			return new HashMap<>();
		}
	}
}
