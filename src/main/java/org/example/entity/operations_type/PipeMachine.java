package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class PipeMachine extends OperationKit {

    public PipeMachine() {
        this.name = "Трубогиб";
        this.color = "#009688";
    }
}
