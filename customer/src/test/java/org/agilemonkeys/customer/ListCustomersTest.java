package org.agilemonkeys.customer;

import io.micronaut.http.MediaType;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import org.agilemonkeys.customer.api.model.Customer;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;
import org.agilemonkeys.customer.persistence.repository.CustomerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@MicronautTest(transactional = false)
class ListCustomersTest {

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
    @DisplayName("Should return HTTP.200 and empty list if there isn't any customer")
    void shouldReturnHTTP200AndEmptyListIfNoMovements() {
        RestAssured.given()
                .get("/customers")
                .then()
                .log()
                .all()
                .statusCode(200)
                .body("customers.size()", is(0));
    }

    @Test
    @DisplayName("Should return HTTP.200 and customer list if there is a customer")
    void shouldReturnHTTP200AndCustomerListIfNoMovements() {
        var entityCustomer = new CustomerEntity();
        entityCustomer.setName("Francisco");
        entityCustomer.setSurname("Lopez");
        entityCustomer.setDocumentId("54353453Y");

        var savedCustomer = customerRepository.save(entityCustomer);


        var customerList = RestAssured.given()
                .get("/customers")
                .then()
                .log()
                .all()
                .statusCode(200)
                .extract()
                .body()
                .jsonPath().getList(".", Customer.class);

        assertThat(customerList, notNullValue());
        assertThat(customerList.size(), is(1));
        assertThat(customerList.get(0).getCustomerId(), notNullValue());
        assertThat(customerList.get(0).getName(), is("Francisco"));
        assertThat(customerList.get(0).getSurname(), is("Lopez"));
        assertThat(customerList.get(0).getDocumentId(), is("54353453Y"));
    }

    //Pagination Tests

    @Test
    @DisplayName("Should set default elements per page if not defined")
    public void shouldSetDefaultElementsPerPageIfNotDefined() {

        for (int i = 0; i <= 49; i++) {
            var entityCustomer = new CustomerEntity();
            entityCustomer.setName("Francisco");
            entityCustomer.setSurname("Lopez");
            entityCustomer.setDocumentId("54353453Y");

            customerRepository.save(entityCustomer);
        }

        RestAssured.given()
                .get("/customers")
                .then()
                .log()
                .all()
                .statusCode(200)
                .body("pageNumber", is(0))
                .body("totalSize", is(50))
                .body("totalPages", is(3))
                .body("customers.size()", is(20));
    }

    @Test
    @DisplayName("Should return 2 page and set default elements per page")
    public void shouldReturn2PageAndSetDefaultElementsPerPage() {

        for (int i = 0; i <= 49; i++) {
            var entityCustomer = new CustomerEntity();
            entityCustomer.setName("Francisco");
            entityCustomer.setSurname("Lopez");
            entityCustomer.setDocumentId("54353453Y");

            customerRepository.save(entityCustomer);
        }

        RestAssured.given()
                .get("/customers?page=1")
                .then()
                .log()
                .all()
                .statusCode(200)
                .body("pageNumber", is(1))
                .body("totalSize", is(50))
                .body("totalPages", is(3))
                .body("movements.size()", is(20));

    }


    @Test
    @DisplayName("Should return one element per page and all as total page")
    public void shouldReturnOneElementPerPageAndAllAsTotalPage() {

        for (int i = 0; i <= 49; i++) {
            var entityCustomer = new CustomerEntity();
            entityCustomer.setName("Francisco");
            entityCustomer.setSurname("Lopez");
            entityCustomer.setDocumentId("54353453Y");

            customerRepository.save(entityCustomer);
        }

        RestAssured.given()
                .get("/customers?elementsPerPage=1")
                .then()
                .log()
                .all()
                .statusCode(200)
                .body("pageNumber", is(0))
                .body("totalSize", is(50))
                .body("totalPages", is(50))
                .body("movements.size()", is(1));

    }


    @Test
    @DisplayName("Should return 1 element per page and page 20")
    public void shouldReturn1ElementPerPageAndPage20() {

        for (int i = 0; i <= 49; i++) {
            var entityCustomer = new CustomerEntity();
            entityCustomer.setName("Francisco");
            entityCustomer.setSurname("Lopez");
            entityCustomer.setDocumentId("54353453Y");

            customerRepository.save(entityCustomer);
        }

        RestAssured.given()
                .get("/customers?page=20&elementsPerPage=1")
                .then()
                .log()
                .all()
                .statusCode(200)
                .body("pageNumber", is(20))
                .body("totalSize", is(50))
                .body("totalPages", is(50))
                .body("movements.size()", is(1));
    }
}
