package org.agilemonkeys.customer.persistence.dao;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
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

    /**
     * Save an entity into the repository
     * <p>
     * If the entity id is not informed it will create a new one.
     * Otherwise, the entity will be updated.
     *
     * @param customerEntity The entity to be saved
     * @return The saved customer entity
     */
    @Override
    public CustomerEntity saveCustomer(CustomerEntity customerEntity) {
        if (customerEntity.getId() == null)
            return customerRepository.save(customerEntity);
        else
            return customerRepository.update(customerEntity);
    }

    /**
     * Find a customer into the database by his identifier
     *
     * @param customerId The customer identifier
     * @return The customer if it exists.
     */
    @Override
    public Optional<CustomerEntity> findCustomerById(Long customerId) {
        return customerRepository.findById(customerId);
    }

    /**
     * Delete a customer entity from the database
     *
     * @param customerEntity The customer entity object
     */
    @Override
    public void deleteCustomer(CustomerEntity customerEntity) {
        customerRepository.delete(customerEntity);
    }

    /**
     * Return all the customers in the database
     *
     * @return The customer entity list
     */
    @Override
    public List<CustomerEntity> findAll() {
        List<CustomerEntity> entitiesList = new ArrayList<>();
        customerRepository.findAll().iterator().forEachRemaining(entitiesList::add);
        return entitiesList;
    }
}
