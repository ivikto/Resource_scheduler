package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class PipeMachine extends OperationType {

    public PipeMachine() {
        this.name = "Трубогиб";
        this.color = "#009688";
    }
}
