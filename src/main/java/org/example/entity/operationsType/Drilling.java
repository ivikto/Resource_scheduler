package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class Drilling extends OperationType {

    public Drilling() {
        this.name = "Сверление";
    }
}
