package proPets.lostFound.configuration;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@PropertySource({ "classpath:application.properties" })
@EnableJpaRepositories(basePackages = "proPets.lostFound.dao.LostFoundJPARepository", transactionManagerRef = "transactionManager")
@EnableTransactionManagement

public class PostgreSQLRepoConfiguration {
	
	@Autowired
	private Environment env;
	
	private static final String[] ENTITYMANAGER_PACKAGES_TO_SCAN = { "proPets.lostFound.model" };

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {

	LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
	entityManagerFactoryBean.setJpaVendorAdapter(vendorAdaptor());
	entityManagerFactoryBean.setDataSource(dataSource());
	entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
	entityManagerFactoryBean.setPackagesToScan(ENTITYMANAGER_PACKAGES_TO_SCAN);
	entityManagerFactoryBean.setJpaProperties(addProperties());
	return entityManagerFactoryBean;
	}
	
	@Bean (name="transactionManager")
	public JpaTransactionManager jpaTransactionManager() {
	JpaTransactionManager transactionManager = new JpaTransactionManager();
	transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
	return transactionManager;
	}
 
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
 
		String username = env.getProperty("spring.datasource.username");
		String password = env.getProperty("spring.datasource.password");
		String driverClass = env.getProperty("spring.datasource.driver-class-name");
		String url = env.getProperty("spring.datasource.url");
 
		return DataSourceBuilder.create().username(username).password(password).url(url).driverClassName(driverClass)
				.build();
	}
 
	private HibernateJpaVendorAdapter vendorAdaptor() {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		return vendorAdapter;
	}
	
	private Properties addProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto", env.getProperty("spring.jpa.hibernate.ddl-auto"));
		properties.setProperty("hibernate.dialect", env.getProperty("spring.jpa.properties.hibernate.dialect"));
		properties.setProperty("hibernate.show_sql", env.getProperty("spring.jpa.show-sql"));

		return properties;
	}
  
}
