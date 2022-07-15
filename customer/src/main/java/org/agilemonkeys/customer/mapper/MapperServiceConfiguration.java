package org.agilemonkeys.customer.mapper;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;
import com.github.dozermapper.core.loader.api.BeanMappingBuilder;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;
import org.agilemonkeys.customer.api.model.Customer;
import org.agilemonkeys.customer.api.model.SaveCustomerRequest;
import org.agilemonkeys.customer.persistence.entity.CustomerEntity;

@Singleton
public class MapperServiceConfiguration implements MapperService {
    private Mapper mapper;

    @Override
    public Mapper getMapper() {
        return mapper;
    }

    @PostConstruct
    public void initDozer() {
        mapper = DozerBeanMapperBuilder.create()
                .withMappingBuilder(new BeanMappingBuilder() {
                    @Override
                    protected void configure() {
                        mapping(SaveCustomerRequest.class, CustomerEntity.class);
                        mapping(CustomerEntity.class, Customer.class)
                                .fields("id", "customerId");


                    }
                }).build();
    }
}