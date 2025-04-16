package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class TurningMachine extends OperationType {

    public TurningMachine() {
        this.name = "Токарный";
    }
}
