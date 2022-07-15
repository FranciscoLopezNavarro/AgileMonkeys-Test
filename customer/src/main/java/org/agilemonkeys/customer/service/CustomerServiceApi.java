package org.agilemonkeys.customer.service;

import org.agilemonkeys.customer.api.model.Customer;
import org.agilemonkeys.customer.api.model.CustomersPaginatedList;
import org.agilemonkeys.customer.api.model.SaveCustomerRequest;

public interface CustomerServiceApi {
    /**
     * Creates a new customer
     *
     * @param saveCustomerRequest The request with the customer data
     * @return The saved customer
     */
    Customer createCustomer(SaveCustomerRequest saveCustomerRequest);

    /**
     * Get all the information of a Customer given his id.
     *
     * @param customerId The customer identifier
     * @return The customer and all his information.
     */
    Customer getCustomerDetail(Long customerId);

    /**
     * Updates an existing customer
     * <p>
     * Throws an exception if the customer does not exist.
     *
     * @param customerId The customer identifier
     * @return The updated customer
     */
    Customer updateCustomer(Long customerId, SaveCustomerRequest saveCustomerRequest);

    /**
     * Delete a customer given its id.
     * <p>
     * As an idempotent method, DELETE won't to anything if the customer does not exits.
     *
     * @param customerId The customer identifier
     */
    void deleteCustomer(Long customerId);

    /**
     * Returns a list with all the customers in the system.
     *
     * @return A paginated list that contains:
     * * * The current page number
     * * * The total size of elements in database
     * * * The total pages generated based on the pagination fields given in the request
     * * * A list with the found customers. By default, an empty one if no customers found.
     */
    CustomersPaginatedList getAllCustomers(Integer page, Integer elementsPerPage);
}
