package com.example.codingexercise.dto;

import java.util.Map;

/**
 * Record for currency exchange data from https://www.frankfurter.app/latest?from=
 * @author tomd
 *
 */
public record CurrencyExchanger(double amount, String base, String date, Map<String, Double> rates) {
}
