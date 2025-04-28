package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class TurningMachine extends OperationType {

    public TurningMachine() {
        this.name = "Токарный";
        this.color = "#8BC34A";
    }
}
