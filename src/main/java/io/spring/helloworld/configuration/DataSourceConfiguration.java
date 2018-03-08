package io.spring.helloworld.configuration;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * @author David Turanski
 **/
@Configuration
public class DataSourceConfiguration {

	@Bean
	public DataSource businessDataSource(
		@Value("${external.datasource.driver-class-name}") String driverClassName,
		@Value("${external.datasource.url}") String url,
		@Value("${external.datasource.username}") String username,
		@Value("${external.datasource.password}") String password
	){
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}

	@Bean
	@Primary
	public DataSource batchDataSource(
		@Value("${spring.datasource.driver-class-name}") String driverClassName,
		@Value("${spring.datasource.url}") String url,
		@Value("${spring.datasource.username}") String username,
		@Value("${spring.datasource.password}") String password
	){
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName(driverClassName);
		dataSource.setJdbcUrl(url);
		dataSource.setUsername(username);
		dataSource.setPassword(password);
		return dataSource;
	}
}
