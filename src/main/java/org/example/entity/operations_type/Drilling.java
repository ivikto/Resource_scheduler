package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class Drilling extends OperationType {

    public Drilling() {
        this.name = "Сверление";
        this.color = "#2196F3";
    }
}
