package org.agilemonkeys.customer;

import io.micronaut.http.MediaType;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;
import org.agilemonkeys.customer.persistence.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@MicronautTest(transactional = false)
class DeleteCustomerTest {

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
    @DisplayName("Should return HTTP.405 if customerId is empty")
    void shouldReturnHTTP405IfCustomerIdIsEmpty() {
        RestAssured.given()
                .pathParams("customerId", "")
                .delete("/customers/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(405);
    }

    @Test
    @DisplayName("Should return HTTP.204 if customer does not exists (Delete = Idempotent method)")
    void shouldReturnHTTP204IfCustomerDoesNotExists() {
        RestAssured.given()
                .pathParams("customerId", "2")
                .delete("/customers/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(204);
    }

    @Test
    @DisplayName("Should return HTTP.204 and delete customer if it exists")
    void shouldReturnHTTP204AndCreateNewCustomer() {

        var entityCustomer = new CustomerEntity();
        entityCustomer.setName("Francisco");
        entityCustomer.setSurname("Lopez");
        entityCustomer.setDocumentId("54353453Y");

        var savedCustomer = customerRepository.save(entityCustomer);

        var customer = RestAssured.given()
                .pathParams("customerId", savedCustomer.getId())
                .delete("/customers/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(204);

        assertThat(customerRepository.findById(savedCustomer.getId()), is(Optional.empty()));
    }
}
