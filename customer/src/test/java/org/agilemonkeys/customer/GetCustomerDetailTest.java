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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@MicronautTest
class GetCustomerDetailTest {

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


    @Test
    @DisplayName("Should return HTTP.404 if customerId is empty")
    void shouldReturnHTTP404IfCustomerIdIsEmpty() {
        RestAssured.given()
                .pathParams("customerId", "")
                .get("/customer/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(404);

    }

    @Test
    @DisplayName("Should return HTTP.404 if customer does not exists")
    void shouldReturnHTTP404IfCustomerDoesNotExists() {
        RestAssured.given()
                .pathParams("customerId", "2")
                .get("/customer/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(404)
                .body("message", is("Customer not found."));

    }

    @Test
    @DisplayName("Should return HTTP.200 and customer if it exists")
    void shouldReturnHTTP201AndCreateNewCustomer() {

        var entityCustomer = new CustomerEntity();
        entityCustomer.setName("Francisco");
        entityCustomer.setSurname("Lopez");
        entityCustomer.setDocumentId("54353453Y");

        var savedCustomer = customerRepository.save(entityCustomer);

        var customer = RestAssured.given()
                .pathParams("customerId", savedCustomer.getId())
                .get("/customer/{customerId}")
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body().as(Customer.class);

        assertThat(customer, notNullValue());
        assertThat(customer.getCustomerId(), notNullValue());
        assertThat(customer.getName(), is("Francisco"));
        assertThat(customer.getSurname(), is("Lopez"));
        assertThat(customer.getDocumentId(), is("54353453Y"));
        assertThat(savedCustomer.getCreatedDate(), InstantMatchers.before(Instant.now()));
    }
}
