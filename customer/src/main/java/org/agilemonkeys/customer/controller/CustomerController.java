package org.agilemonkeys.customer.controller;

import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.security.annotation.Secured;
import jakarta.inject.Inject;
import org.agilemonkeys.customer.api.model.Customer;
import org.agilemonkeys.customer.api.model.CustomersPaginatedList;
import org.agilemonkeys.customer.api.model.SaveCustomerRequest;
import org.agilemonkeys.customer.service.CustomerServiceApi;

import static io.micronaut.security.rules.SecurityRule.IS_AUTHENTICATED;

@ExecuteOn(TaskExecutors.IO)
@Secured(IS_AUTHENTICATED)
@Controller(value = "/customers")
public class CustomerController {
    private final CustomerServiceApi customerService;

    @Inject
    public CustomerController(CustomerServiceApi customerService) {
        this.customerService = customerService;
    }

    /**
     * Creates a new customer
     *
     * @param saveCustomerRequest The request with the customer data
     * @return The saved customer
     */
    @Post(
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Customer> saveCustomer(@Body SaveCustomerRequest saveCustomerRequest) {
        return HttpResponse.status(HttpStatus.CREATED).body(customerService.createCustomer(saveCustomerRequest));
    }

    /**
     * Returns a list with all the customers in the system.
     *
     * @return The Customer list
     */
    @Get(
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<CustomersPaginatedList> getCustomers(@Nullable @QueryValue Integer page,
                                                             @Nullable @QueryValue Integer elementsPerPage) {
        return HttpResponse.status(HttpStatus.OK).body(customerService.getAllCustomers(page, elementsPerPage));
    }

    /**
     * Get all the information of a Customer given his id.
     *
     * @param customerId The customer identifier
     * @return The customer and all his information.
     */
    @Get(value = "/{customerId}",
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Customer> getCustomerDetail(@PathVariable Long customerId) {
        return HttpResponse.status(HttpStatus.OK).body(customerService.getCustomerDetail(customerId));
    }

    /**
     * Updates an existing customer
     * <p>
     * Throws an exception if the customer does not exist.
     *
     * @param customerId The customer identifier
     * @return The updated customer
     */
    @Put(value = "/{customerId}",
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Customer> updateCustomer(@PathVariable Long customerId, @Body SaveCustomerRequest saveCustomerRequest) {
        return HttpResponse.status(HttpStatus.OK).body(customerService.updateCustomer(customerId, saveCustomerRequest));
    }

    /**
     * Delete a customer given its id.
     * <p>
     * As an idempotent method, DELETE won't to anything if the customer does not exits.
     *
     * @param customerId The customer identifier
     */
    @Delete(value = "/{customerId}",
            processes = MediaType.APPLICATION_JSON,
            consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<Void> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return HttpResponse.noContent();
    }
}
