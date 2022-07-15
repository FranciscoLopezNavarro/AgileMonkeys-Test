package org.agilemonkeys.customer.api.model;

import io.micronaut.core.annotation.Introspected;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Introspected
public class CustomersPaginatedList implements Serializable {

    List<Customer> customers = Collections.emptyList();
    private int pageNumber;
    private long totalSize;
    private int totalPages;

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
