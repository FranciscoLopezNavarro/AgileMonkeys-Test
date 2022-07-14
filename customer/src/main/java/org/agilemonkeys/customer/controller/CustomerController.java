package org.agilemonkeys.customer.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import jakarta.inject.Inject;
import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.api.SaveCustomerRequest;
import org.agilemonkeys.customer.service.CustomerServiceApi;

import java.util.List;

@ExecuteOn(TaskExecutors.IO)
@Controller(value = "/customers")
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
        return HttpResponse.status(HttpStatus.CREATED).body(customerService.createCustomer(saveCustomerRequest));
    }

    @Get(
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<List<Customer>> getCustomers() {
        return HttpResponse.status(HttpStatus.OK).body(customerService.getCustomers());
    }


    @Get(value = "/{customerId}",
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Customer> getCustomerDetail(@PathVariable Long customerId) {
        return HttpResponse.status(HttpStatus.OK).body(customerService.getCustomerDetail(customerId));
    }

    @Put(value = "/{customerId}",
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Customer> updateCustomer(@PathVariable Long customerId, @Body SaveCustomerRequest saveCustomerRequest) {
        return HttpResponse.status(HttpStatus.OK).body(customerService.updateCustomer(customerId, saveCustomerRequest));
    }

    @Delete(value = "/{customerId}",
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return HttpResponse.noContent();
    }
}
