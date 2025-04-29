package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class Printer extends OperationKit {

    public Printer() {
        this.name = "3D печать";
        this.color = "#E91E63";
    }
}
