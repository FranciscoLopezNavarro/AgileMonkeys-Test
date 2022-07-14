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

@MicronautTest(transactional = false)
class SaveCustomerTest {

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
    @DisplayName("Should return HTTP.400 if name is empty")
    void shouldReturnHTTP400IfNameIsEmpty() {
        RestAssured.given()
                .body("{\n" +
                        "  \"name\": \"\",\n" +
                        "  \"surname\": \"Lopez\",\n" +
                        "  \"documentId\": \"543214G\"\n" +
                        "}")
                .post("/customers")
                .then()
                .log()
                .all()
                .statusCode(400)
                .body("message", is("The customer name is mandatory."));

    }

    @Test
    @DisplayName("Should return HTTP.400 if surname is empty")
    void shouldReturnHTTP400IfSurnameIsEmpty() {
        RestAssured.given()
                .body("{\n" +
                        "  \"name\": \"Francisco\",\n" +
                        "  \"surname\": \"\",\n" +
                        "  \"documentId\": \"543214G\"\n" +
                        "}")
                .post("/customers")
                .then()
                .log()
                .all()
                .statusCode(400)
                .body("message", is("The customer surname is mandatory."));

    }

    @Test
    @DisplayName("Should return HTTP.400 if documentId is empty")
    void shouldReturnHTTP400IfDocumentIdIsEmpty() {
        RestAssured.given()
                .body("{\n" +
                        "  \"name\": \"Francisco\",\n" +
                        "  \"surname\": \"Lopez\",\n" +
                        "  \"documentId\": \"\"\n" +
                        "}")
                .post("/customers")
                .then()
                .log()
                .all()
                .statusCode(400)
                .body("message", is("The customer documentId is mandatory."));

    }

    @Test
    @DisplayName("Should return HTTP.201 and create new customer")
    void shouldReturnHTTP201AndCreateNewCustomer() {
        var savedCustomer = RestAssured.given()
                .body("{\n" +
                        "  \"name\": \"Francisco\",\n" +
                        "  \"surname\": \"Lopez\",\n" +
                        "  \"documentId\": \"54353453Y\"\n" +
                        "}")
                .post("/customers")
                .then()
                .log()
                .all()
                .statusCode(201)
                .extract()
                .body().as(Customer.class);

        assertThat(savedCustomer, notNullValue());
        assertThat(savedCustomer.getCustomerId(), notNullValue());
        assertThat(savedCustomer.getName(), is("Francisco"));
        assertThat(savedCustomer.getSurname(), is("Lopez"));
        assertThat(savedCustomer.getDocumentId(), is("54353453Y"));
        assertThat(savedCustomer.getCreatedDate(), InstantMatchers.before(Instant.now()));
    }

    @Test
    @DisplayName("Should return HTTP.409 if there already exists one customer with the same documentId")
    void shouldReturnHTTP409IfCustomerWithSameCustomerIdExists() {

        var entityCustomer = new CustomerEntity();
        entityCustomer.setName("Francisco");
        entityCustomer.setSurname("Lopez");
        entityCustomer.setDocumentId("54353453Y");

        customerRepository.save(entityCustomer);


        var savedCustomer = RestAssured.given()
                .body("{\n" +
                        "  \"name\": \"Francisco\",\n" +
                        "  \"surname\": \"Lopez\",\n" +
                        "  \"documentId\": \"54353453Y\"\n" +
                        "}")
                .post("/customers")
                .then()
                .log()
                .all()
                .statusCode(409)
                .body("message", is("Customer with documentId: 54353453Y already exists in the system."));
    }
}
