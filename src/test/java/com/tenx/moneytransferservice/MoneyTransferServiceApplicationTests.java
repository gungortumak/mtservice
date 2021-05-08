package com.tenx.moneytransferservice;

import com.tenx.moneytransferservice.controller.TransferTransactionDTO;
import com.tenx.moneytransferservice.model.Account;
import com.tenx.moneytransferservice.model.Currency;
import com.tenx.moneytransferservice.model.TransferTransaction;
import com.tenx.moneytransferservice.service.AccountService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.skyscreamer.jsonassert.JSONAssert;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoneyTransferServiceApplicationTests {

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	@Autowired
	AccountService accountService;

	private final static Long SOURCE_ACCOUNT_ID = 101L;
	private final static Long TARGET_ACCOUNT_ID = 102L;
	private final static Long TARGET_ACCOUNT_USD_ID = 103L;


	@Test
	@DisplayName("Not Existing Source Account")
	public void testTransferNotExistingSourceAccount() throws Exception {
		Long notExistedSourceAccountId = 700L;
		Long notExistedTargetAccountId = 800L;
		String expectedMessage = "{\n" +
				"    \"message\": \"Account does not exist : " + notExistedSourceAccountId + "\"\n" +
				"}";
		TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
				.targetAccountId(notExistedTargetAccountId)
				.sourceAccountId(notExistedSourceAccountId)
				.amount(new BigDecimal(500)).build();

		HttpHeaders headers = new HttpHeaders();

		HttpEntity<TransferTransactionDTO> entity = new HttpEntity<TransferTransactionDTO>(transferTransactionDTO, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createPath("/v1/transfers"),
				HttpMethod.POST, entity, String.class);
		Assertions.assertTrue(response.getStatusCode().is4xxClientError());
		Assertions.assertEquals(MediaType.APPLICATION_JSON,response.getHeaders().getContentType());
		JSONAssert.assertEquals(expectedMessage, response.getBody(), false);

	}

	@Test
	@DisplayName("Not Existing Target Account")
	public void testTransferNotExistingTargetAccount() throws Exception {
		Long notExistedTargetAccountId = 800L;
		String expectedMessage = "{\n" +
				"    \"message\": \"Account does not exist : " + notExistedTargetAccountId + "\"\n" +
				"}";
		TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
				.targetAccountId(notExistedTargetAccountId)
				.sourceAccountId(SOURCE_ACCOUNT_ID)
				.amount(new BigDecimal(500)).build();


		HttpHeaders headers = new HttpHeaders();

		HttpEntity<TransferTransactionDTO> entity = new HttpEntity<TransferTransactionDTO>(transferTransactionDTO, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createPath("/v1/transfers"),
				HttpMethod.POST, entity, String.class);
		Assertions.assertTrue(response.getStatusCode().is4xxClientError());
		Assertions.assertEquals(MediaType.APPLICATION_JSON,response.getHeaders().getContentType());
		JSONAssert.assertEquals(expectedMessage, response.getBody(), false);


	}

	@Test
	@DisplayName("InSufficent Balance")
	public void testInsufficentBalanceSourceAccount() throws Exception {
		String expectedMessage = "{\n" +
				"    \"message\": \"Insufficent Balance For : " + SOURCE_ACCOUNT_ID +"\"\n" +
				"}";
		TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
				.targetAccountId(TARGET_ACCOUNT_ID)
				.sourceAccountId(SOURCE_ACCOUNT_ID)
				.amount(new BigDecimal(5000)).build();


		HttpHeaders headers = new HttpHeaders();

		HttpEntity<TransferTransactionDTO> entity = new HttpEntity<TransferTransactionDTO>(transferTransactionDTO, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createPath("/v1/transfers"),
				HttpMethod.POST, entity, String.class);
		Assertions.assertTrue(response.getStatusCode().is4xxClientError());
		Assertions.assertEquals(MediaType.APPLICATION_JSON,response.getHeaders().getContentType());
		JSONAssert.assertEquals(expectedMessage, response.getBody(), false);

	}

	@Test
	@DisplayName("Transfer Between Same Account")
	public void testTransferBetweenSameAccounts() throws Exception {
		String expectedMessage = "{\n" +
				"    \"message\": \"Transfer between same account is not possible : " + SOURCE_ACCOUNT_ID + "\"\n" +
				"}";
		TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
				.targetAccountId(SOURCE_ACCOUNT_ID)
				.sourceAccountId(SOURCE_ACCOUNT_ID)
				.amount(new BigDecimal(100)).build();


		HttpHeaders headers = new HttpHeaders();

		HttpEntity<TransferTransactionDTO> entity = new HttpEntity<TransferTransactionDTO>(transferTransactionDTO, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createPath("/v1/transfers"),
				HttpMethod.POST, entity, String.class);
		Assertions.assertTrue(response.getStatusCode().is4xxClientError());
		Assertions.assertEquals(MediaType.APPLICATION_JSON,response.getHeaders().getContentType());
		JSONAssert.assertEquals(expectedMessage, response.getBody(), false);

	}

	@Test
	@DisplayName("Happy Transfer")
	public void testHappyTransferBetweenAccounts() {

		TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
				.targetAccountId(TARGET_ACCOUNT_ID)
				.sourceAccountId(SOURCE_ACCOUNT_ID)
				.amount(new BigDecimal(100)).build();


		HttpHeaders headers = new HttpHeaders();

		HttpEntity<TransferTransactionDTO> entity = new HttpEntity<TransferTransactionDTO>(transferTransactionDTO, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createPath("/v1/transfers"),
				HttpMethod.POST, entity, String.class);
		Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
		Assertions.assertNotNull(response.getHeaders().getLocation());
		Assertions.assertTrue(response.getHeaders().getLocation().toString().matches(".*transfers/[0-9]"));

	}
	@Test
	@DisplayName("Transfer Between Currencies")
	public void testTransferBetweenDifferentCurrencies() throws Exception {
		String expectedMessage = "{\n" +
				"    \"message\": \"Source and Target account has currency mismatch. Source Currency : GBP Target Currency : USD\"\n" +
				"}";
		TransferTransactionDTO transferTransactionDTO = TransferTransactionDTO.builder()
				.targetAccountId(TARGET_ACCOUNT_USD_ID)
				.sourceAccountId(SOURCE_ACCOUNT_ID)
				.amount(new BigDecimal(100)).build();


		HttpHeaders headers = new HttpHeaders();

		HttpEntity<TransferTransactionDTO> entity = new HttpEntity<TransferTransactionDTO>(transferTransactionDTO, headers);

		ResponseEntity<String> response = restTemplate.exchange(
				createPath("/v1/transfers"),
				HttpMethod.POST, entity, String.class);
		Assertions.assertTrue(response.getStatusCode().is4xxClientError());
		Assertions.assertEquals(MediaType.APPLICATION_JSON,response.getHeaders().getContentType());
		JSONAssert.assertEquals(expectedMessage, response.getBody(), false);

	}
	private String createPath(String uri) {
		return "http://localhost:" + port + uri;
	}

}
