package com.example.codingexercise.service;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.example.codingexercise.dto.CurrencyExchanger;

/**
 * Service used to access the currency exchange API.
 * Its main function is to get the exchange rate between two currencies.
 * @author tomd
 *
 */
@Component
public class CurrencyExchangeRateService {

	private final RestTemplate restTemplate;

	@Autowired
	public CurrencyExchangeRateService(@Qualifier("currencyExchangeRateRestTemplate") final RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Gets the exchange rate to convert form source to target currency).
	 * 
	 * @param source String currency name as defined by
	 *               https://www.frankfurter.app/latest?from
	 * @param target String currency name as defined by
	 *               https://www.frankfurter.app/latest?from
	 * @return returns the exchange rate on success or -1.0 if an exchange rate
	 *         could not be found.
	 */
	public double getCurrencyExchangeRate(String source, String target) {

		double exchangeRate = -1;
		if (source == null || source.length() < 3 || target == null || target.length() < 3) {
			return -1;
		}
		if (source.equalsIgnoreCase(target)) {
			return 1.0d;
		}
		try {
			CurrencyExchanger xChanger = restTemplate.getForObject("https://www.frankfurter.app/latest?from={source}",
				CurrencyExchanger.class, source.toUpperCase());
			if (xChanger.base().equalsIgnoreCase(source)) {
				Map<String, Double> exchangeMap = xChanger.rates();
				if (exchangeMap != null && exchangeMap.containsKey(target.toUpperCase())) {
					exchangeRate = exchangeMap.get(target.toUpperCase());
				}
			}
		} catch (RestClientException ex) {
			exchangeRate = -1;
		}
		return exchangeRate;
	}

}