package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class Paint extends OperationType {

    public Paint() {
        this.name = "Станция окраски";
        this.color = "#FFC107";
    }
}
