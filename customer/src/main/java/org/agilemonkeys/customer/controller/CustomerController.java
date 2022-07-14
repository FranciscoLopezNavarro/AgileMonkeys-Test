package org.agilemonkeys.customer.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;
import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.api.SaveCustomerRequest;
import org.agilemonkeys.customer.service.CustomerServiceApi;

@ExecuteOn(TaskExecutors.IO)
@Controller(value = "/customer")
public class CustomerController {

    private final CustomerServiceApi customerService;

    @Inject
    public CustomerController(CustomerServiceApi customerService) {
        this.customerService = customerService;
    }

    @Post(
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Customer> saveCustomer(@Body SaveCustomerRequest saveCustomerRequest) {

        return HttpResponse.status(HttpStatus.CREATED).body(customerService.saveCustomer(saveCustomerRequest));
    }
3213122131231
}
