package org.agilemonkeys.customer.service;

import io.micronaut.core.util.StringUtils;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.exceptions.HttpStatusException;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.agilemonkeys.customer.api.error.CustomError;
import org.agilemonkeys.customer.api.model.Customer;
import org.agilemonkeys.customer.api.model.CustomersPaginatedList;
import org.agilemonkeys.customer.api.model.SaveCustomerRequest;
import org.agilemonkeys.customer.mapper.MapperService;
import org.agilemonkeys.customer.persistence.dao.CustomerDaoServiceApi;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;

import java.util.Objects;
import java.util.Optional;

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
     * @return The updated customer
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

    /**
     * Returns a list with all the customers in the system.
     *
     * @return A paginated list that contains:
     * * * The current page number
     * * * The total size of elements in database
     * * * The total pages generated based on the pagination fields given in the request
     * * * A list with the found customers. By default, an empty one if no customers found.
     */
    @Override
    public CustomersPaginatedList getAllCustomers(Integer page, Integer elementsPerPage) {

        var customerEntitiesPaged = customerDaoService.findAll(definePagination(page, elementsPerPage));
        if (customerEntitiesPaged.getContent().isEmpty())
            return mapPageToPaginatedListResponse(Page.empty());

        return mapPageToPaginatedListResponse(customerEntitiesPaged);
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

    /**
     * Determine the pagination based on the page and elements per page.
     * <p>
     * If no pagination is not defined, we are going to ignore the pagination. If only page is filled,
     * we are going to set 20 element per page.
     * <p>
     * Finally, if both page and elements are filled, the pagination is going to define
     * the requested page and split based on the elements number desired in the request.
     *
     * @param page            The page number
     * @param elementsPerPage the elements per page
     * @return The search pagination
     */
    private Pageable definePagination(Integer page, Integer elementsPerPage) {
        return Pageable.from(Objects.requireNonNullElse(page, 0), Objects.requireNonNullElse(elementsPerPage, 20));
    }


    /**
     * Build the paginated result object from the Page object obtained from database
     *
     * @param pagedCustomerList The paginated result found in database
     * @return A mapped search response.
     */
    private CustomersPaginatedList mapPageToPaginatedListResponse(Page<CustomerEntity> pagedCustomerList) {
        var response = new CustomersPaginatedList();
        response.setPageNumber(pagedCustomerList.getPageNumber());
        response.setTotalSize(pagedCustomerList.getTotalSize());
        response.setTotalPages(Math.max(pagedCustomerList.getTotalPages(), 1));
        response.setCustomers(
                pagedCustomerList
                        .map(customerEntity -> mapperService.getMapper().map(customerEntity, Customer.class))
                        .getContent());
        return response;
    }

}
