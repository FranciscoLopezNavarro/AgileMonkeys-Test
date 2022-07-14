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
     * Save a customer
     *
     * @param saveCustomerRequest The request with the customer data
     * @return The saved customer
     */
    @Override
    public Customer saveCustomer(SaveCustomerRequest saveCustomerRequest) {
        validateSaveCustomerRequest(saveCustomerRequest);

        var savedCustomer = createCustomer(saveCustomerRequest);
        return mapCustomerEntityToCustomerDTO(savedCustomer);
    }

    /**
     * Get all the information of a Customer given his id.
     * <p>
     * Throws an exception if the customer does not exist.
     *
     * @param customerId The customer identifier
     * @return The customer and all his information.
     */
    @Override
    public Customer getCustomerDetail(Long customerId) {
        var customer = customerDaoService.findCustomerById(customerId);
        if (customer.isEmpty())
            throw new HttpStatusException(HttpStatus.NOT_FOUND, new CustomError("Customer not found."));

        return mapCustomerEntityToCustomerDTO(customer.get());
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


    /**
     * Creates a customer into the database.
     * <p>
     * If a Customer with the same documentId throws and exception
     *
     * @param saveCustomerRequest The request with the customer data
     * @return The saved customer
     */
    private CustomerEntity createCustomer(SaveCustomerRequest saveCustomerRequest) {
        var sameIdCustomer = customerDaoService.findCustomerByDocumentId(saveCustomerRequest.getDocumentId());

        if (sameIdCustomer.isPresent())
            throw new HttpStatusException(HttpStatus.CONFLICT, new CustomError("Customer with documentId: " + saveCustomerRequest.getDocumentId() + " already exists in the system."));

        return customerDaoService.saveCustomer(mapperService.getMapper().map(saveCustomerRequest, CustomerEntity.class));
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
     * Build a Customer object from a CustomerEntity object
     *
     * @param customerEntity the customer entity object to be mapped
     * @return the Customer object
     */
    private Customer mapCustomerEntityToCustomerDTO(CustomerEntity customerEntity) {
        return mapperService.getMapper().map(customerEntity, Customer.class);
    }
}
