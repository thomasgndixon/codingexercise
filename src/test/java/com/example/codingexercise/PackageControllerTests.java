package com.example.codingexercise;

//import com.example.codingexercise.controller.PackageController;
import com.example.codingexercise.model.ProductPackage;
import com.example.codingexercise.repository.PackageRepository;
import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests the Package Controller rest API.
 */
//@SpringBootTest(classes=com.example.codingexercise.controller.PackageController.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)


// @ExtendWith(SpringExtension.class)
// @RunWith(SpringRunner.class)
//@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes=com.example.codingexercise.CodingExerciseApplication.class)
class PackageControllerTests {

	
	private final TestRestTemplate restTemplate;
    private final PackageRepository packageRepository;
	/**
	 * Valid Product IDs from https://product-service.herokuapp.com/api/v1/products
	 */
	private final String [] PRODUCT_IDS = { "7dgX6XzU3Wds", "DXSQpv6XVeJm" };
	/**
	 * Valid product prices that corresponding to the products above.
	 */
	private final double [] PRODUCT_PRICE = {899.0, 999.0};

    @Autowired
    PackageControllerTests(TestRestTemplate restTemplate, PackageRepository packageRepository) {
		this.restTemplate = restTemplate;
        this.packageRepository = packageRepository;
    }

	/**
	 * Verify creation of new product package. Note ... the product package
	 * repository verifies that the product id existing on the product service.
	 * Hence you must use a real product id to run the test.
	 */
	@Test
	void createPackage() {
		ResponseEntity<ProductPackage> created = restTemplate.postForEntity("/packages",
				new ProductPackage(null, "Test Name", "Test Desc", List.of(PRODUCT_IDS[0]), PRODUCT_PRICE[0]),
				ProductPackage.class);
		assertEquals(HttpStatus.OK, created.getStatusCode(), "Unexpected status code");
		ProductPackage createdBody = created.getBody();
		assertNotNull(createdBody, "Unexpected body");
		assertEquals("Test Name", createdBody.getName(), "Unexpected name");
		assertEquals("Test Desc", createdBody.getDescription(), "Unexpected description");
		assertEquals(List.of("7dgX6XzU3Wds"), createdBody.getProductIds(), "Unexpected products");

		ProductPackage productPackage = packageRepository.get(createdBody.getId());
		assertNotNull(productPackage, "Unexpected package");
		assertEquals(createdBody.getId(), productPackage.getId(), "Unexpected id");
		assertEquals(createdBody.getName(), productPackage.getName(), "Unexpected name");
		assertEquals(createdBody.getDescription(), productPackage.getDescription(), "Unexpected description");
		assertEquals(createdBody.getProductIds(), productPackage.getProductIds(), "Unexpected products");
		assertEquals(createdBody.getTotalPrice(), productPackage.getTotalPrice(), "Unexpected product price");

	}

	/**
	 * Verify get package for single product in the product list.
	 */
    @Test
    void getPackage() {
        ProductPackage productPackage = packageRepository.create("Test Name 2", "Test Desc 2", List.of(PRODUCT_IDS[1]));
        ResponseEntity<ProductPackage> fetched = restTemplate.getForEntity("/packages/{id}", ProductPackage.class, productPackage.getId());
        assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
        ProductPackage fetchedBody = fetched.getBody();
        assertNotNull(fetchedBody, "Unexpected body");
        assertEquals(productPackage.getId(), fetchedBody.getId(), "Unexpected id");
        assertEquals(productPackage.getName(), fetchedBody.getName(), "Unexpected name");
        assertEquals(productPackage.getDescription(), fetchedBody.getDescription(), "Unexpected description");
        assertEquals(productPackage.getProductIds(), fetchedBody.getProductIds(), "Unexpected products");
        assertEquals(productPackage.getTotalPrice(), fetchedBody.getTotalPrice(), "Unexpected product price");
    }
    
    /**
     * Verify get package when package has multiple products in the product list. 
     */
	@Test
	void getPackagesWithMutlpleProducts() {
		ProductPackage productPackage = packageRepository.create("Test Name 2", "Test Desc 2",
				List.of(PRODUCT_IDS[0], PRODUCT_IDS[1]));
		ResponseEntity<ProductPackage> fetched = restTemplate.getForEntity("/packages/{id}", ProductPackage.class,
				productPackage.getId());
		assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");
		ProductPackage fetchedBody = fetched.getBody();
		assertNotNull(fetchedBody, "Unexpected body");
		assertEquals(productPackage.getId(), fetchedBody.getId(), "Unexpected id");
		assertEquals(productPackage.getName(), fetchedBody.getName(), "Unexpected name");
		assertEquals(productPackage.getDescription(), fetchedBody.getDescription(), "Unexpected description");
		assertArrayEquals(productPackage.getProductIds().toArray(), fetchedBody.getProductIds().toArray(),
				"Unexpected products");
		assertEquals(productPackage.getTotalPrice(), fetchedBody.getTotalPrice(), "Unexpected product price");
	}

	/**
	 * Verify REST update of product package. 
	 * 		Steps 
	 * 		1.	Create a product package in the product package repository. 
	 * 		2.	Update the product package via rest. 
	 * 		3.	Reads the product package from the package repository and verify updates are present.
	 */
	@Test
	void updatePackage() {
		// Create A package
		ProductPackage initialProductPackage = packageRepository.create("Test Name 3", "Test Desc 3",
				List.of(PRODUCT_IDS[0]));
		ProductPackage updatedProductPackage = new ProductPackage(initialProductPackage.getId(), "Test Name 4",
				"Test Desc 4", List.of(PRODUCT_IDS[1]), PRODUCT_PRICE[1]);
		HttpEntity<ProductPackage> httpEntity = new HttpEntity<>(updatedProductPackage);
		ResponseEntity<ProductPackage> updated = restTemplate.exchange("/packages/" + initialProductPackage.getId(),
				HttpMethod.PUT, httpEntity, ProductPackage.class);
		assertEquals(HttpStatus.OK, updated.getStatusCode(), "Unexpected status code");

		ProductPackage updatedProductPackageInRepository = packageRepository.get(initialProductPackage.getId());

		// Changed all the fields via rest put. Only the ids should the same. Check
		// fields are different
		assertEquals(initialProductPackage.getId(), updatedProductPackageInRepository.getId(), "Unexpected id");
		assertNotEquals(initialProductPackage.getName(), updatedProductPackageInRepository.getName(),
				"Unexpected name");
		assertNotEquals(initialProductPackage.getDescription(), updatedProductPackageInRepository.getDescription(),
				"Unexpected description");
		assertNotEquals(initialProductPackage.getProductIds().get(0),
				updatedProductPackageInRepository.getProductIds().get(0), "Unexpected products");
		assertNotEquals(initialProductPackage.getTotalPrice(), updatedProductPackageInRepository.getTotalPrice(),
				"Unexpected product price");

		// Verify actual update values.
		assertEquals("Test Name 4", updatedProductPackageInRepository.getName(), "Unexpected name");
		assertEquals("Test Desc 4", updatedProductPackageInRepository.getDescription(), "Unexpected description");
		assertArrayEquals(List.of(PRODUCT_IDS[1]).toArray(),
				updatedProductPackageInRepository.getProductIds().toArray(), "Unexpected products");
		assertEquals(PRODUCT_PRICE[1], updatedProductPackageInRepository.getTotalPrice(), "Unexpected product price");

	}

	/**
	 * Verify REST delete of product package. 
	 * 	Steps 1. 
	 * 		1.	Create a product package in the product package repository. 
	 * 		2.  Delete the product package via rest. 
	 * 		3.	Reads the product package from the package repository and verify its not present present.
	 * 		4.	Delete is Omnipotent so repeat the delete for the same ID and it should return HttpStatus.NO_CONTENT.
	 */
	@Test
	void deletePackage() {
		// Create A package to delete
		ProductPackage productPackage = packageRepository.create("Test Name 5", "Test Desc 5",
				List.of(PRODUCT_IDS[0], PRODUCT_IDS[1]));
		String productPackageId = productPackage.getId(); // Save id so we can check it is gone.

		ResponseEntity<ProductPackage> updated = restTemplate.exchange("/packages/" + productPackageId,
				HttpMethod.DELETE, new HttpEntity<HttpHeaders>(new HttpHeaders()), ProductPackage.class);

		assertEquals(HttpStatus.OK, updated.getStatusCode(), "Unexpected status code");
		ProductPackage p = packageRepository.get(productPackageId);
		assertTrue(p == null); // check Product Package is delete in the repository.

		// Repeat the delete for the same id, it is omnipotent so deleting multiple has no effect.
		// Delete request should return HttpStatus.NO_CONTENT when object does not exist.
		updated = restTemplate.exchange("/packages/" + productPackageId, HttpMethod.DELETE,
				new HttpEntity<HttpHeaders>(new HttpHeaders()), ProductPackage.class);

		assertEquals(HttpStatus.NO_CONTENT, updated.getStatusCode(), "Unexpected status code");
	}

	/**
	 * Verify getting the entire list of packages in the repository.
	 */
	@Test
	void listPackages() {
		ProductPackage productPackage1 = packageRepository.create("Test Name 1", "Test Desc 1",
				List.of(PRODUCT_IDS[0]));
		ProductPackage productPackage2 = packageRepository.create("Test Name 2", "Test Desc 2",
				List.of(PRODUCT_IDS[0], PRODUCT_IDS[1]));

		// Verify valid response when fetching list of all products.
		ResponseEntity<Object> fetched = restTemplate.getForEntity("/packages", Object.class);
		assertEquals(HttpStatus.OK, fetched.getStatusCode(), "Unexpected status code");

		// Verify the contents of the response.
		String bodyText = fetched.getBody().toString();
		assertTrue(bodyText.contains(productPackage1.getId()));
		assertTrue(bodyText.contains(productPackage2.getId()));
		assertTrue(bodyText.contains(productPackage1.getName()));
		assertTrue(bodyText.toString().contains(productPackage2.getName()));
		assertTrue(bodyText.contains(productPackage1.getDescription()));
		assertTrue(bodyText.contains(productPackage2.getDescription()));
		assertTrue(bodyText.contains("productIds=[" + PRODUCT_IDS[0] + "]"));
		assertTrue(bodyText.contains("productIds=[" + PRODUCT_IDS[0] + ", " + PRODUCT_IDS[1] + "]"));
		assertTrue(bodyText.contains("totalPrice=" + PRODUCT_PRICE[0]));
		assertTrue(bodyText.contains("totalPrice=" + (PRODUCT_PRICE[0] + PRODUCT_PRICE[1])));

	}

}
