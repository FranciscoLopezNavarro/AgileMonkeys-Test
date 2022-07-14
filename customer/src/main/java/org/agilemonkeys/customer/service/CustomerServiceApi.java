package org.agilemonkeys.customer.service;

import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.api.SaveCustomerRequest;

import java.util.List;

public interface CustomerServiceApi {
    
    Customer createCustomer(SaveCustomerRequest saveCustomerRequest);

    Customer getCustomerDetail(Long customerId);

    Customer updateCustomer(Long customerId, SaveCustomerRequest saveCustomerRequest);

    void deleteCustomer(Long customerId);

    List<Customer> getCustomers();
}
