package org.agilemonkeys.customer.service;

import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.api.SaveCustomerRequest;

public interface CustomerServiceApi {
    Customer createCustomer(SaveCustomerRequest saveCustomerRequest);

    Customer getCustomerDetail(Long customerId);


    Customer updateCustomer(Long customerId, SaveCustomerRequest saveCustomerRequest);

    void deleteCustomer(Long customerId);

}
