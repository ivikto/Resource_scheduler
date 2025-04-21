package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class MillingMachine extends OperationType {

    public MillingMachine() {
        this.name = "Фрезеровка";
        this.color = "#607D8B";
    }
}
