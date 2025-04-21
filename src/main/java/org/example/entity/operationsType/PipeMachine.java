package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class PipeMachine extends OperationType {

    public PipeMachine() {
        this.name = "Трубогиб";
        this.color = "#009688";
    }
}
