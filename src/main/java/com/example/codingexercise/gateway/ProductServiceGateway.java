package com.example.codingexercise.gateway;

import com.example.codingexercise.gateway.dto.Product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductServiceGateway {

    private final RestTemplate restTemplate;

    @Autowired
    public ProductServiceGateway(@Qualifier("productBasicAutoRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Product getProduct(String id) {
        return restTemplate.getForObject("https://user:pass@product-service.herokuapp.com/api/v1/products/{id}", Product.class, id);
    }
}
