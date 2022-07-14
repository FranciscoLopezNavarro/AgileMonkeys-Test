package org.agilemonkeys.customer.persistence.dao;

import org.agilemonkeys.customer.persistence.entity.CustomerEntity;

import java.util.Optional;

public interface CustomerDaoServiceApi {
    CustomerEntity saveCustomer(CustomerEntity customerEntity);

    Optional<CustomerEntity> findCustomerByDocumentId(String documentId);

    Optional<CustomerEntity> findCustomerById(Long customerId);

    void deleteCustomer(CustomerEntity customerEntity);
}
