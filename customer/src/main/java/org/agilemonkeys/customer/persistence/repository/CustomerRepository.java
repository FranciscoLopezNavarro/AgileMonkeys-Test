package org.agilemonkeys.customer.persistence.repository;

import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;

import java.util.Optional;

@Repository
public interface CustomerRepository extends PageableRepository<CustomerEntity, Long> {

    Page<CustomerEntity> findAll(Pageable pageable);
}
