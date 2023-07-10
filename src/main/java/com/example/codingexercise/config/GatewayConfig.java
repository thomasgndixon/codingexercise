package com.example.codingexercise.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

/**
 * Configures the rest template used to access the Product gateway.
 * Uses basic authentication.
 * @author tomd
 *
 */
@Configuration
@PropertySource("classpath:productgateway.properties")
public class GatewayConfig {

	@Value("${product.gateway.user}")
	private String user;
	@Value("${product.gateway.password}")
	private String pass;

	@Bean(name = "productBasicAutoRestTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplateBuilder().basicAuthentication(user, pass).build();
	}
}
