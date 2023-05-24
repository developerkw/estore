package com.developerkw.estore;

import com.developerkw.estore.model.ModelUtil;
import com.developerkw.estore.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductApplicationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldCreateANewProduct() {
		var newProduct = ModelUtil.createProduct(4L, "Mac Book Pro", "Laptop", 120,
			new BigDecimal("9800"), Set.of("20%OFF"));

		ResponseEntity<Product> createResponse = restTemplate
			.withBasicAuth("bullish", "abc123")
			.postForEntity("/product", newProduct, Product.class);
		assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		long id = createResponse.getBody().getId();
		ResponseEntity<Product> getResponse = restTemplate
			.withBasicAuth("bullish", "abc123")
			.getForEntity("/product/" + id, Product.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

		var productResponse = getResponse.getBody();
		assertEquals(4L, productResponse.getId());
		assertEquals("Mac Book Pro", productResponse.getName());
	}

	@Test
	void shouldReturnAProduct() {
		ResponseEntity<Product> response = restTemplate
			.withBasicAuth("bullish", "abc123")
			.getForEntity("/product/1", Product.class);

		assertEquals(HttpStatus.OK, response.getStatusCode());

		var productResponse = response.getBody();
		assertEquals(1L, productResponse.getId());
		assertEquals("Samsung Fold 4", productResponse.getName());
		assertEquals("Mobile", productResponse.getCategory());
		assertEquals(328, productResponse.getStock());
		assertEquals(new BigDecimal("13800.00"), productResponse.getPrice());
		assertEquals(Set.of("BUY_3_GET_1_FREE"), productResponse.getDiscounts());
	}

	@Test
	void shouldNotReturnProductWithInvalidId() {
		ResponseEntity<Product> response = restTemplate
			.withBasicAuth("bullish", "abc123")
			.getForEntity("/product/988", Product.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void shouldRejectIfNotHavingAdminRole() {
		ResponseEntity<Product> response = restTemplate
			.withBasicAuth("testuser", "qrs456")
			.getForEntity("/product/1", Product.class);

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void shouldRejectIfIncorrectCredential() {
		ResponseEntity<Product> response = restTemplate
			.withBasicAuth("invalilduser", "wrongpassword")
			.getForEntity("/product/1", Product.class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void shouldUpdateAnExistingProductWithNewDiscount() {
		ResponseEntity<Product> response = restTemplate
			.withBasicAuth("bullish", "abc123")
			.getForEntity("/product/1", Product.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		var productResponse = response.getBody();
		assertEquals(1L, productResponse.getId());
		assertEquals(Set.of("BUY_3_GET_1_FREE"), productResponse.getDiscounts());

		var newSetOfDiscounts = Set.of("BUY_3_GET_1_FREE", "20%OFF");
		productResponse.setDiscounts(newSetOfDiscounts);
		HttpEntity<Product> request = new HttpEntity<>(productResponse);
		ResponseEntity<Void> updateResponse = restTemplate
			.withBasicAuth("bullish", "abc123")
			.exchange("/product", HttpMethod.PUT, request, Void.class);
		assertThat(updateResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<Product> responseAfterUpdate = restTemplate
			.withBasicAuth("bullish", "abc123")
			.getForEntity("/product/1", Product.class);
		assertEquals(HttpStatus.OK, responseAfterUpdate.getStatusCode());
		var productResponseAfterUpdate = response.getBody();
		assertEquals(1L, productResponseAfterUpdate.getId());
		assertEquals(newSetOfDiscounts, productResponseAfterUpdate.getDiscounts());
	}

	@Test
	void shouldDeleteAnExistingProduct() {
		ResponseEntity<Void> response = restTemplate
			.withBasicAuth("bullish", "abc123")
			.exchange("/product/1", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<Product> getResponse = restTemplate
			.withBasicAuth("bullish", "abc123")
			.getForEntity("/product/1", Product.class);
		assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldNotDeleteAProductThatDoesNotExist() {
		ResponseEntity<Void> deleteResponse = restTemplate
			.withBasicAuth("bullish", "abc123")
			.exchange("/product/666", HttpMethod.DELETE, null, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

}
