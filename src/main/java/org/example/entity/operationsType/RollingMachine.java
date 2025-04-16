package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class RollingMachine extends OperationType {

    public RollingMachine() {
        this.name = "Вальцеватель";
    }
}
