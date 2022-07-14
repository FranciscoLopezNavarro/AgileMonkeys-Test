package org.agilemonkeys.customer.service;

import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.api.SaveCustomerRequest;

public interface CustomerServiceApi {
    Customer saveCustomer(SaveCustomerRequest saveCustomerRequest);

    Customer getCustomerDetail(Long customerId);

    void deleteCustomer(Long customerId);
}
