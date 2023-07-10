package com.example.codingexercise.controller;

import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import com.example.codingexercise.service.CurrencyExchangeRateService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * Rest controller for Product Packages.
 *
 */
@RestController
public class PackageController {
	/**
	 * The base currency used for product prices.
	 */
	private static String BASE_CURRENCY = "USD";
	
	private final PackageRepository packageRepository;

	@Autowired
	private CurrencyExchangeRateService currencyExchangeRateService;

	@Autowired
	public PackageController(PackageRepository packageRepository) {
		this.packageRepository = packageRepository;
	}

	/**
	 * Gets a list of all ProductPackages in the ProductPackate repository.
	 * 
	 * @return the full list of ProductPackages in the product package repository.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/packages")
	public ResponseEntity<List<ProductPackage>> getAllPackages(@RequestBody @RequestParam(required = false) String currencyToUse) {
		return new ResponseEntity<>(convertListOfProductPackagePrice(packageRepository.getAll(), currencyToUse), HttpStatus.OK);
	}

	/**
	 * Creates a new ProductPackage and adds it to the ProductPackage Repository.
	 * 
	 * @param newProductPackage the new ProductPackage to add to the repository.
	 * 
	 * @return On success returns the ProductProduct package with HttpStatus.OK. On
	 *         failure, returns an empty response body with status
	 *         HttpStatus.BAD_REQUEST.
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/packages")
	public ResponseEntity<ProductPackage> create(@RequestBody ProductPackage newProductPackage) {
		ProductPackage productPackage = packageRepository.create(newProductPackage.getName(),
				newProductPackage.getDescription(), newProductPackage.getProductIds());
		return new ResponseEntity<>(productPackage, productPackage == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);

	}

	/**
	 * Gets a product package using its id.
	 * 
	 * @param id String representing the package id.
	 * 
	 * @return On success returns the ProductProduct package with HttpStatus.OK. *
	 *         On failure, returns an empty response body with status
	 *         HttpStatus.BAD_REQUEST.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/packages/{id}")
	public ResponseEntity<ProductPackage> get(@PathVariable String id, @RequestParam(required = false) String currencyToUse) {
		ProductPackage productPackage = convertProductPackagePrice(packageRepository.get(id), currencyToUse);
		return new ResponseEntity<>(productPackage, productPackage == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
	}

	/**
	 * Uses the package id to update the attributes of existing ProductPackage.
	 * 
	 * @param updatedProductPackage new productPackage details that will be added.
	 * 
	 * @return On success, returns the updated ProductPackage with HttpStatus.OK. On
	 *         failure, returns an empty response body with status
	 *         HttpStatus.BAD_REQUEST.
	 */
	@RequestMapping(method = RequestMethod.PUT, value = "/packages/{id}")
	public ResponseEntity<ProductPackage> put(@RequestBody ProductPackage updatedProductPackage) {
		ProductPackage productPackage = packageRepository.update(updatedProductPackage.getId(),
				updatedProductPackage.getName(), updatedProductPackage.getDescription(),
				updatedProductPackage.getProductIds());
		return new ResponseEntity<>(productPackage, productPackage == null ? HttpStatus.BAD_REQUEST : HttpStatus.OK);
	}

	/**
	 * Idempotent delete of a product package (from the package repository).
	 * 
	 * @param id String representing the package id of the package to be deleted.
	 * 
	 * @return On success, returns the deleted ProductPackage with status
	 *         HttpStatus.OK. On failure returns empty response body with status
	 *         HttpStatus.NO_CONTENT.
	 */
	@RequestMapping(method = RequestMethod.DELETE, value = "/packages/{id}")
	public ResponseEntity<Void> delete(@PathVariable String id) {
		ProductPackage productPackage = packageRepository.delete(id);
		return new ResponseEntity<>(productPackage == null ? HttpStatus.NO_CONTENT : HttpStatus.OK);
	}

	/**
	 * Converts a given amount of BASE_CURRENCY to the target currency. Currency
	 * names are as given by https://www.frankfurter.app/latest?from=
	 * 
	 * @param targetCurrency String defining the currency name.
	 * 
	 * @return returns the amount converted to the new currency. If the new currency
	 *         is unknown or not defined then the amount is returned without
	 *         conversion.
	 */
	private double convertBaseCurrency(double amount, String targetCurrency) {

		if (targetCurrency == null || BASE_CURRENCY.equals(targetCurrency.toUpperCase())) {
			return amount;
		}
		// Round result to 2 decimal places.
		return Math.round(amount
				* currencyExchangeRateService.getCurrencyExchangeRate(BASE_CURRENCY, targetCurrency.toUpperCase())
				* 100.0) / 100.0;
	}

	/**
	 * Given a list of product packages with total product prices in the base currency, this
	 * method converts these prices to a different currency.
	 * 
	 * @param productPackages List of product packages.
	 * @param currencyToUse String value names are as given by https://www.frankfurter.app/latest?from=
	 * 
	 * @return List of product packages with the total prices of each package updated to the new currency.
	 */
	private List<ProductPackage> convertListOfProductPackagePrice(List<ProductPackage> productPackages,
			String currencyToUse) {
		List<ProductPackage> updatedProductPackages = new ArrayList<>();
		for (ProductPackage p : productPackages) {
			if (p != null) {
				updatedProductPackages.add(convertProductPackagePrice(p, currencyToUse));
			}
		}
		return updatedProductPackages;
	}

	/**
	 * Given a single product packages with total product price in the base currency, this
	 * method converts the price to a different currency.
	 * 
	 * @param productPackage product packages who's total price will be converted.
	 * @param currencyToUse String value names are as given by https://www.frankfurter.app/latest?from=
	 * 
	 * @return A product package with the total prices of each package updated to the new currency.
	 */
	private ProductPackage convertProductPackagePrice(ProductPackage productPackage, String currencyToUse) {
		// Note Cannot change the product package directly as we have a reference to it.
		if (productPackage == null || currencyToUse == null) {
			return productPackage; // don't convert.
		}
		double convertedProductPrice = convertBaseCurrency(productPackage.getTotalPrice(), currencyToUse);
		if (convertedProductPrice < 0) {
			return productPackage; // conversion failed so return original.
		}

		return new ProductPackage(productPackage.getId(), productPackage.getName(), productPackage.getDescription(),
				productPackage.getProductIds(), convertedProductPrice);
	}
}
