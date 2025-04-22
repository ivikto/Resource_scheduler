package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class Printer extends OperationType {

    public Printer() {
        this.name = "3D печать";
        this.color = "#E91E63";
    }
}
