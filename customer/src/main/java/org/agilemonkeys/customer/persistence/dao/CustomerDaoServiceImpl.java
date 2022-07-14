package org.agilemonkeys.customer.persistence.dao;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.agilemonkeys.customer.api.Customer;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;
import org.agilemonkeys.customer.persistence.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
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
        if (customerEntity.getId() == null)
            return customerRepository.save(customerEntity);
        else
            return customerRepository.update(customerEntity);
    }

    @Override
    public Optional<CustomerEntity> findCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public void deleteCustomer(CustomerEntity customerEntity) {
        customerRepository.delete(customerEntity);
    }

    @Override
    public List<CustomerEntity> findAll() {
        List<CustomerEntity> entitiesList = new ArrayList<>();
        customerRepository.findAll().iterator().forEachRemaining(entitiesList::add);
        return entitiesList;
    }
}
