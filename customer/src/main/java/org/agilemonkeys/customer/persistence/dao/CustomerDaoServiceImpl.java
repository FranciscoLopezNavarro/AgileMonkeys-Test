package org.agilemonkeys.customer.persistence.dao;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;
import org.agilemonkeys.customer.persistence.repository.CustomerRepository;

import java.util.Optional;

@Singleton
public class CustomerDaoServiceImpl implements CustomerDaoServiceApi {

    private final CustomerRepository customerRepository;

    @Inject
    public CustomerDaoServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) {
        return customerRepository.save(customerEntity);
    }

    @Override
    public Optional<CustomerEntity> findCustomerByDocumentId(String documentId) {
        return customerRepository.findByDocumentId(documentId);
    }

    @Override
    public Optional<CustomerEntity> findCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }
}
