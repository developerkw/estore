package com.developerkw.estore;

import com.developerkw.estore.model.BasketItem;
import com.developerkw.estore.model.ModelUtil;
import com.developerkw.estore.model.ReceiptDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BasketApplicationTests {

	@Autowired
	DataLoader dataLoader;
	@Autowired
	TestRestTemplate restTemplate;

	@Test
	void shouldCreateANewBasketItem() {
		var sampleProductList = dataLoader.getSampleProductList();
		//Then create a new basket item
		var newBasketItem = ModelUtil.createBasket("testUser", sampleProductList.get(2), 2);

		ResponseEntity<BasketItem> createBasketResponse = restTemplate
			.withBasicAuth("testuser", "qrs456")
			.postForEntity("/basket", newBasketItem, BasketItem.class);
		assertThat(createBasketResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

		var basketResponse = createBasketResponse.getBody();
		assertEquals(3L, basketResponse.getId());
		assertEquals(sampleProductList.get(2), basketResponse.getProduct());
		assertEquals(2, basketResponse.getQuantity());
	}

	@Test
	void shouldReturnProducts() {
		ResponseEntity<BasketItem[]> response = restTemplate
			.withBasicAuth("testuser", "qrs456")
			.getForEntity("/basket", BasketItem[].class);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		var basketResponse = response.getBody();
		assertEquals(2, basketResponse.length);
		assertEquals(1, basketResponse[0].getId());
		assertEquals("testuser", basketResponse[0].getUserName());
		assertEquals(dataLoader.getSampleProductList().get(0).getId(), basketResponse[0].getProduct().getId());
		assertEquals(4, basketResponse[0].getQuantity());
	}

	@Test
	void shouldRejectIfNotHavingCustomerRole() {
		ResponseEntity<BasketItem[]> response = restTemplate
			.withBasicAuth("bullish", "abc123")
			.getForEntity("/basket", BasketItem[].class);

		assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void shouldRejectIfIncorrectCredential() {
		ResponseEntity<BasketItem[]> response = restTemplate
			.withBasicAuth("invalilduser", "wrongpassword")
			.getForEntity("/basket", BasketItem[].class);

		assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void shouldReturnNotFoundIfNoItem() {
		ResponseEntity<BasketItem[]> response = restTemplate
			.withBasicAuth("testuser2", "efg789")
			.getForEntity("/basket", BasketItem[].class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}

	@Test
	void shouldDeleteAnExistingBasketItem() {
		ResponseEntity<Void> response = restTemplate
			.withBasicAuth("testuser", "qrs456")
			.exchange("/basket/1", HttpMethod.DELETE, null, Void.class);
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

		ResponseEntity<BasketItem[]> getResponse = restTemplate
			.withBasicAuth("testuser", "qrs456")
			.getForEntity("/basket", BasketItem[].class);

		assertEquals(HttpStatus.OK, getResponse.getStatusCode());
		var basketResponse = getResponse.getBody();
		assertEquals(1, basketResponse.length);
	}

	@Test
	void shouldNotDeleteABasketItemThatDoesNotExist() {
		ResponseEntity<Void> deleteResponse = restTemplate
			.withBasicAuth("testuser2", "efg789")
			.exchange("/basket/1", HttpMethod.DELETE, null, Void.class);
		assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
	}

	@Test
	void shouldGenerateCorrectReceipt() {
		ResponseEntity<ReceiptDto> response = restTemplate
			.withBasicAuth("testuser", "qrs456")
			.getForEntity("/basket/checkout", ReceiptDto.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		var receiptResponse = response.getBody();
		assertEquals(2, receiptResponse.getPurchases().size());
		assertEquals("testuser", receiptResponse.getUsername());
		assertEquals(new BigDecimal("41400.00"), receiptResponse.getPurchases().get(0).getDiscountedTotalPrice());
		assertEquals(new BigDecimal("8580.00"), receiptResponse.getPurchases().get(1).getDiscountedTotalPrice());
		assertEquals(new BigDecimal("49980.00"), receiptResponse.getTotalPrice());
	}

	@Test
	void shouldReturnNotFoundIfNoItemToCheckout() {
		ResponseEntity<ReceiptDto> response = restTemplate
			.withBasicAuth("testuser2", "efg789")
			.getForEntity("/basket/checkout", ReceiptDto.class);

		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertNull(response.getBody());
	}
}
