package org.agilemonkeys.customer.api.model;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class SaveCustomerRequest {
    private String name;
    private String surname;
    private String documentId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }
}
