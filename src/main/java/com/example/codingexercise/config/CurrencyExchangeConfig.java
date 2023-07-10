package com.example.codingexercise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Configures the rest template used for accessing currency exchange rates.
 * @author tomd
 *
 */
@Configuration
public class CurrencyExchangeConfig {

	@Bean(name = "currencyExchangeRateRestTemplate")
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}