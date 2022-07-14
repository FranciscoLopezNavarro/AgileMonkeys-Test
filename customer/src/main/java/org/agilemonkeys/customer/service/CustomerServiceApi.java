package org.agilemonkeys.customer.service;

import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.api.SaveCustomerRequest;

import java.util.List;

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
     * @return The Customer list
     */
    List<Customer> getCustomers();
}
