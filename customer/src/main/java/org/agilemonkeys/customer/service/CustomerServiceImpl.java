package org.agilemonkeys.customer.service;

import io.micronaut.core.util.StringUtils;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.api.SaveCustomerRequest;
import org.agilemonkeys.customer.api.error.CustomError;
import org.agilemonkeys.customer.mapper.MapperService;
import org.agilemonkeys.customer.persistence.dao.CustomerDaoServiceApi;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Singleton
public class CustomerServiceImpl implements CustomerServiceApi {
    private final CustomerDaoServiceApi customerDaoService;
    private final MapperService mapperService;

    @Inject
    public CustomerServiceImpl(CustomerDaoServiceApi customerDaoService, MapperService mapperService) {
        this.customerDaoService = customerDaoService;
        this.mapperService = mapperService;
    }

    /**
     * Creates a new customer
     *
     * @param saveCustomerRequest The request with the customer data
     * @return The saved customer
     */
    @Override
    public Customer createCustomer(SaveCustomerRequest saveCustomerRequest) {
        validateSaveCustomerRequest(saveCustomerRequest);
        var customerToSave = mapCustomerEntityFromSaveCustomerRequest(saveCustomerRequest);

        return mapCustomerEntityToCustomerDTO(saveCustomer(customerToSave));
    }


    /**
     * Get all the information of a Customer given his id.
     *
     * @param customerId The customer identifier
     * @return The customer and all his information.
     */
    @Override
    public Customer getCustomerDetail(Long customerId) {
        var customer = getCustomerIfExists(customerId);
        if (customer.isEmpty())
            throw new HttpStatusException(HttpStatus.NOT_FOUND, new CustomError("Customer not found."));

        return mapCustomerEntityToCustomerDTO(customer.get());
    }


    /**
     * Updates an existing customer
     * <p>
     * Throws an exception if the customer does not exist.
     *
     * @param customerId The customer identifier
     */
    @Override
    public Customer updateCustomer(Long customerId, SaveCustomerRequest saveCustomerRequest) {

        var customer = getCustomerIfExists(customerId);
        if (customer.isEmpty())
            throw new HttpStatusException(HttpStatus.NOT_FOUND, new CustomError("Customer not found."));

        validateSaveCustomerRequest(saveCustomerRequest);
        mapperService.getMapper().map(saveCustomerRequest, customer.get());

        return mapCustomerEntityToCustomerDTO(saveCustomer(customerDaoService.saveCustomer(customer.get())));
    }

    /**
     * Delete a customer given its id.
     * <p>
     * As an idempotent method, DELETE won't to anything if the customer does not exits.
     *
     * @param customerId The customer identifier
     */
    @Override
    public void deleteCustomer(Long customerId) {
        customerDaoService.findCustomerById(customerId).ifPresent(customerDaoService::deleteCustomer);
    }

    @Override
    public List<Customer> getCustomers() {
        return customerDaoService.findAll().stream().map(this::mapCustomerEntityToCustomerDTO).collect(Collectors.toList());
    }


    /**
     * Validate the customer save request fields
     *
     * @param saveCustomerRequest the request object to be validated
     */
    private void validateSaveCustomerRequest(SaveCustomerRequest saveCustomerRequest) {
        if (StringUtils.isEmpty(saveCustomerRequest.getName()))
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, new CustomError("The customer name is mandatory."));

        if (StringUtils.isEmpty(saveCustomerRequest.getSurname()))
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, new CustomError("The customer surname is mandatory."));

        if (StringUtils.isEmpty(saveCustomerRequest.getDocumentId()))
            throw new HttpStatusException(HttpStatus.BAD_REQUEST, new CustomError("The customer documentId is mandatory."));
    }


    /**
     * Get all the information of a Customer given his id.
     *
     * @param customerId The customer identifier
     * @return The customer and all his information.
     */
    private Optional<CustomerEntity> getCustomerIfExists(Long customerId) {
        return customerDaoService.findCustomerById(customerId);
    }

    /**
     * Saves a Customer Entity object into database
     *
     * @param customerEntity the customer entity object to be saved
     * @return the Customer object
     */
    private CustomerEntity saveCustomer(CustomerEntity customerEntity) {
        return customerDaoService.saveCustomer(customerEntity);
    }

    /**
     * Build a Customer object from a CustomerEntity object
     *
     * @param customerEntity the customer entity object to be mapped
     * @return the Customer object
     */
    private Customer mapCustomerEntityToCustomerDTO(CustomerEntity customerEntity) {
        return mapperService.getMapper().map(customerEntity, Customer.class);
    }

    /**
     * Build a Customer Entity from a Save Customer request
     *
     * @param saveCustomerRequest the request object
     * @return the Customer entity object
     */
    private CustomerEntity mapCustomerEntityFromSaveCustomerRequest(SaveCustomerRequest saveCustomerRequest) {
        return mapperService.getMapper().map(saveCustomerRequest, CustomerEntity.class);
    }

}
