package org.agilemonkeys.customer;

import io.micronaut.http.MediaType;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;
import org.agilemonkeys.customer.persistence.repository.CustomerRepository;
import org.exparity.hamcrest.date.InstantMatchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@MicronautTest(transactional = false)
class UpdateCustomerTest {

    @Inject
    private EmbeddedServer server;

    @Inject
    CustomerRepository customerRepository;

    @PostConstruct
    private void setup() {
        RestAssured.requestSpecification = null;
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri(server.getURI())
                .setContentType(MediaType.APPLICATION_JSON)
                .setAccept(MediaType.APPLICATION_JSON)
                .build()
                .log()
                .all();
    }

    @AfterEach
    private void cleanContext() {
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Should return HTTP.405 customerId is not informed")
    void shouldReturnHTTP405IfCustomerIdIsEmpty() {
        RestAssured.given()
                .pathParams("customerId", "")
                .body("{\n" +
                        "  \"name\": \"\",\n" +
                        "  \"surname\": \"Lopez\",\n" +
                        "  \"documentId\": \"543214G\"\n" +
                        "}")
                .put("/customers/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(405);

    }

    @Test
    @DisplayName("Should return HTTP.404 if customer does not exists")
    void shouldReturnHTTP404IfCustomerDoesNotExists() {
        RestAssured.given()
                .pathParams("customerId", "12")
                .body("{\n" +
                        "  \"name\": \"\",\n" +
                        "  \"surname\": \"Lopez\",\n" +
                        "  \"documentId\": \"543214G\"\n" +
                        "}")
                .put("/customers/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(404)
                .body("message", is("Customer not found."));

    }

    @Test
    @DisplayName("Should return HTTP.400 if name is empty")
    void shouldReturnHTTP400IfNameIsEmpty() {
        var entityCustomer = new CustomerEntity();
        entityCustomer.setName("Francisco");
        entityCustomer.setSurname("Lopez");
        entityCustomer.setDocumentId("54353453Y");

        var savedCustomer = customerRepository.save(entityCustomer);

        RestAssured.given()
                .pathParams("customerId", savedCustomer.getId())
                .body("{\n" +
                        "  \"name\": \"\",\n" +
                        "  \"surname\": \"Lopez\",\n" +
                        "  \"documentId\": \"543214G\"\n" +
                        "}")
                .put("/customers/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(400)
                .body("message", is("The customer name is mandatory."));

    }


    @Test
    @DisplayName("Should return HTTP.400 if surname is empty")
    void shouldReturnHTTP400IfSurnameIsEmpty() {
        var entityCustomer = new CustomerEntity();
        entityCustomer.setName("Francisco");
        entityCustomer.setSurname("Lopez");
        entityCustomer.setDocumentId("54353453Y");

        var savedCustomer = customerRepository.save(entityCustomer);

        RestAssured.given()
                .pathParams("customerId", savedCustomer.getId())
                .body("{\n" +
                        "  \"name\": \"Francisco\",\n" +
                        "  \"surname\": \"\",\n" +
                        "  \"documentId\": \"543214G\"\n" +
                        "}")
                .put("/customers/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(400)
                .body("message", is("The customer surname is mandatory."));

    }

    @Test
    @DisplayName("Should return HTTP.400 if documentId is empty")
    void shouldReturnHTTP400IfDocumentIdIsEmpty() {
        var entityCustomer = new CustomerEntity();
        entityCustomer.setName("Francisco");
        entityCustomer.setSurname("Lopez");
        entityCustomer.setDocumentId("54353453Y");

        var savedCustomer = customerRepository.save(entityCustomer);

        RestAssured.given()
                .pathParams("customerId", savedCustomer.getId())
                .body("{\n" +
                        "  \"name\": \"Francisco\",\n" +
                        "  \"surname\": \"Lopez\",\n" +
                        "  \"documentId\": \"\"\n" +
                        "}")
                .put("/customers/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(400)
                .body("message", is("The customer documentId is mandatory."));

    }

    @Test
    @DisplayName("Should return HTTP.200 and update the  customer")
    void shouldReturnHTTP201AndCreateNewCustomer() {
        var entityCustomer = new CustomerEntity();
        entityCustomer.setName("Francisco");
        entityCustomer.setSurname("Lopez");
        entityCustomer.setDocumentId("111111Y");

        var savedCustomer = customerRepository.save(entityCustomer);

        var updatedCustomer = RestAssured.given()
                .pathParams("customerId", savedCustomer.getId())
                .body("{\n" +
                        "  \"name\": \"Pepe\",\n" +
                        "  \"surname\": \"Martinez\",\n" +
                        "  \"documentId\": \"54353453Y\"\n" +
                        "}")
                .put("/customers/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body().as(Customer.class);

        assertThat(updatedCustomer, notNullValue());
        assertThat(updatedCustomer.getCustomerId(), notNullValue());
        assertThat(updatedCustomer.getName(), is("Pepe"));
        assertThat(updatedCustomer.getSurname(), is("Martinez"));
        assertThat(updatedCustomer.getDocumentId(), is("54353453Y"));
        assertThat(updatedCustomer.getCreatedDate(), InstantMatchers.before(Instant.now()));
        assertThat(updatedCustomer.getUpdatedDate(), InstantMatchers.before(Instant.now()));

    }
}
