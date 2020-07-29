package ru.typik.dc.accountService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import ru.typik.dc.accountService.operations.PutOperation;
import ru.typik.dc.accountService.operations.TakeOperation;
import ru.typik.dc.accountService.operations.TransferOperation;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Testcontainers
public class AccountControllerIntegTest {

    @Container
    static MySQLContainer<?> mySQLContainer = new MySQLContainer<>().withDatabaseName("mydb")
            .withInitScript("data.sql");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) throws Exception {
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.datasource.driver-class-name", () -> mySQLContainer.getDriverClassName());
    }

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testInvalidArguments() {
        assertEquals(HttpStatus.BAD_REQUEST,
                this.restTemplate
                        .postForEntity(getUrl("putMoney"), new PutOperation("7", BigDecimal.TEN.negate()), String.class)
                        .getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, this.restTemplate
                .postForEntity(getUrl("putMoney"), new PutOperation("", BigDecimal.TEN), String.class).getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST, this.restTemplate
                .postForEntity(getUrl("takeMoney"), new TakeOperation("7", BigDecimal.TEN.negate()), String.class)
                .getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST,
                this.restTemplate
                        .postForEntity(getUrl("takeMoney"), new TakeOperation("", BigDecimal.TEN), String.class)
                        .getStatusCode());

        assertEquals(HttpStatus.BAD_REQUEST,
                this.restTemplate
                        .postForEntity(getUrl("transferMoney"),
                                new TransferOperation("1", "2", BigDecimal.TEN.negate()), String.class)
                        .getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, this.restTemplate
                .postForEntity(getUrl("transferMoney"), new TransferOperation("", "2", BigDecimal.TEN), String.class)
                .getStatusCode());
        assertEquals(HttpStatus.BAD_REQUEST, this.restTemplate
                .postForEntity(getUrl("transferMoney"), new TransferOperation("1", "", BigDecimal.TEN), String.class)
                .getStatusCode());
    }

    @Test
    public void testPutMoneyAccountNotFound() {
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUrl("putMoney"),
                new PutOperation("6", BigDecimal.TEN), String.class);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Account 6 was not found", result.getBody());
    }

    @Test
    public void testTakeMoneyAccountNotFound() {
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUrl("takeMoney"),
                new PutOperation("6", BigDecimal.TEN), String.class);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Account 6 was not found", result.getBody());
    }

    @Test
    public void testTransferMoneyAccountNotFoundFrom() {
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUrl("transferMoney"),
                new TransferOperation("6", "1", BigDecimal.TEN), String.class);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Account 6 was not found", result.getBody());
    }

    @Test
    public void testTransferMoneyAccountNotFoundTo() {
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUrl("transferMoney"),
                new TransferOperation("1", "6", BigDecimal.ONE), String.class);

        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Account 6 was not found", result.getBody());
    }

    @Test
    public void testTakeMoneyOverdraft() {
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUrl("takeMoney"),
                new PutOperation("1", BigDecimal.valueOf(2)), String.class);

        assertNotNull(result);
        assertEquals(HttpStatus.PRECONDITION_FAILED, result.getStatusCode());
        assertEquals("Overdraft for account 1 and amount 2", result.getBody());
    }

    @Test
    public void testTransferMoneyOverdraft() {
        ResponseEntity<String> result = this.restTemplate.postForEntity(getUrl("transferMoney"),
                new TransferOperation("1", "2", BigDecimal.valueOf(2)), String.class);

        assertNotNull(result);
        assertEquals(HttpStatus.PRECONDITION_FAILED, result.getStatusCode());
        assertEquals("Overdraft for account 1 and amount 2", result.getBody());
    }

    @Test
    public void testTakeMoneySuccess() {
        ResponseEntity<TakeOperation> result = this.restTemplate.postForEntity(getUrl("takeMoney"),
                new TakeOperation("2", BigDecimal.ONE), TakeOperation.class);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testPutMoneySuccess() {
        ResponseEntity<PutOperation> result = this.restTemplate.postForEntity(getUrl("putMoney"),
                new PutOperation("2", BigDecimal.valueOf(1)), PutOperation.class);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testTransferMoneySuccess() {
        ResponseEntity<TransferOperation> result = this.restTemplate.postForEntity(getUrl("transferMoney"),
                new TransferOperation("3", "2", BigDecimal.ONE), TransferOperation.class);

        assertNotNull(result);
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    private String getUrl(String method) {
        return String.format("http://localhost:%d/%s", this.port, method);
    }

}
