package org.agilemonkeys.customer.persistence.dao;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;

import java.util.List;
import java.util.Optional;

public interface CustomerDaoServiceApi {

    /**
     * Save an entity into the repository
     * <p>
     * If the entity id is not informed it will create a new one.
     * Otherwise, the entity will be updated.
     *
     * @param customerEntity The entity to be saved
     * @return The saved customer entity
     */
    CustomerEntity saveCustomer(CustomerEntity customerEntity);

    /**
     * Find a customer into the database by his identifier
     *
     * @param customerId The customer identifier
     * @return The customer if it exists.
     */
    Optional<CustomerEntity> findCustomerById(Long customerId);

    /**
     * Delete a customer entity from the database
     *
     * @param customerEntity The customer entity object
     */
    void deleteCustomer(CustomerEntity customerEntity);

    /**
     * Return all the customers with pagination in the database
     *
     * @param pageable the pagination configuration
     * @return The customer entity page
     */
    Page<CustomerEntity> findAll(Pageable pageable);
}
