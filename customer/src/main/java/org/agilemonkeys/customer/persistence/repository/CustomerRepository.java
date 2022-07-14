package org.agilemonkeys.customer.persistence.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;

import java.util.Optional;

@Repository
public interface CustomerRepository extends CrudRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByDocumentId(String documentId);
}
