package com.example.codingexercise.repository;

import com.example.codingexercise.gateway.ProductServiceGateway;
import com.example.codingexercise.gateway.dto.Product;
import com.example.codingexercise.model.ProductPackage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Repository used to store product packages.
 * To keep the data consistent, packages are rejected if they
 * use product Ids that are not stored recognized by the Product Service,
 * i.e. product-service.herokuapp.com/api/v1/products/
 * @author tomd
 *
 */
@Component
public class PackageRepository {
	/**
	 * repository for product packages.
	 */
    private final List<ProductPackage> productPackages = new ArrayList<>();
    
    private final ProductServiceGateway productServiceGateway;
    
    @Autowired
    public PackageRepository(ProductServiceGateway productServiceGateway){
    	this.productServiceGateway = productServiceGateway;
    }
    
	/**
	 * Creates a new productPackage item and adds it to the PackageRepository.
	 * 
	 * @param name        non-null and non-empty String giving the package name.
	 * @param description String describing the package.
	 * @param productIds  non-null List of product ids. Each id must be a valid
	 *                    Product Id verifiable by the ProductServiceGateway.
	 *                    
	 * @return newly created ProductPackage or null if the ProductPackage could not
	 *         be created.
	 */
	public ProductPackage create(String name, String description, List<String> productIds) {
		ProductPackage newProductPackage = null;
		if (name != null && name.length() > 0 && productIds != null) {
			List<Product> products = getProducts(productIds);
			if (products != null) {
				newProductPackage = new ProductPackage(UUID.randomUUID().toString(), name, description, productIds,
						getUsdPriceForProductIds(products));
				productPackages.add(newProductPackage);
			}
		}
		return newProductPackage;
	}

	/**
	 * Gets a list of products using their product ids.
	 * 
	 * @param productIds list of product ids.
	 * 
	 * @return a list of product ids if all product ids were found on the product
	 *         gateway server. If any of the product ids were not found then returns
	 *         null.
	 */
	private List<Product> getProducts(List<String> productIds) {
		List<Product> products = new ArrayList<>();
		for (String productId : productIds) {
			Product product = productServiceGateway.getProduct(productId);
			if (product != null) {
				products.add(product);
			} else {
				products = null;
				break;
			}
		}
		return products;
	}

	/**
	 * Gets the total USD price for a list of products.
	 * 
	 * @param products list of products each product is non-null.
	 * 
	 * @return returns
	 */
	private double getUsdPriceForProductIds(List<Product> products) {
		double totalUsdPrice = 0;

		if (products != null) {
			for (Product product : products) {
				totalUsdPrice += product.usdPrice();
			}
		}
		return totalUsdPrice;
	}

	/**
	 * Gets a product package given its id.
	 * 
	 * @param id non-null String id of the product package.
	 * 
	 * @return Returns the productPackage if found else returns null if not found or
	 *         if id was null/empty.
	 *         
	 * SHOULD this method always gets the latest price from the product gateway service 
	 * for the product list. And update the product package to reflect the latest price.
	 * TODO Check if need this.
	 */
	public ProductPackage get(String id) {
		for (ProductPackage p : productPackages) {
			if (p.getId().equals(id)) {
				return p;
			}
		}
		return null;
	}
    
    /**
     * Gets the list of productPackages.
     */
    public List<ProductPackage> getAll() {
    	return productPackages;
    }

	/**
	 * Does a full update of a product package (i.e. all fields in the product
	 * package are updated with new values).
	 * 
	 * @param id          non-null String productPackage id.
	 * @param name        non-null String product package name.
	 * @param description String describing the product package. If set to null then
	 *                    an empty string is stored.
	 * @param productIds  non-null List of Strings containing unique product ids.
	 * 
	 * @return the prodcutPackage that was update with updates included. Returns
	 *         null if the productPackage id does not exist in the product package
	 *         repository.
	 */
	public ProductPackage update(String id, String name, String description, List<String> productIds) {
		ProductPackage productPackage = null;
		if (name != null && name.length() > 0 && productIds != null) {
			for (int i = 0; i < productPackages.size(); i++) {
				if (productPackages.get(i).getId().equals(id)) {
					List<Product> products = getProducts(productIds);
					if (products != null) {
						productPackage = new ProductPackage(id, name, description == null ? "" : description,
								productIds, getUsdPriceForProductIds(products));
						productPackages.add(i, productPackage);
						return productPackage;
					}
				}
			}
		}
		return productPackage;
	}

	/**
	 * Deletes a product package item given its id.
	 * 
	 * @param id non-null String productPackage id.
	 * 
	 * @return On Success returns the ProductPackage that was deleted. Returns null
	 *         if id was null/empty or product package was not found in the
	 *         repository.
	 */
	public ProductPackage delete(String id) {
		if (id != null) {
			for (ProductPackage p : productPackages) {
				if (p.getId().equals(id)) {
					productPackages.remove(p);
					return p;
				}
			}
		}
		return null;
	}
}
