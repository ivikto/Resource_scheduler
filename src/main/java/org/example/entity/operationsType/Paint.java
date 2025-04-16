package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class Paint extends OperationType {

    public Paint() {
        this.name = "Малярка";
    }
}
