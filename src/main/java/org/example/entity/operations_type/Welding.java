package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class Welding extends OperationType {

    public Welding() {
        this.name = "Сварка";
        this.color = "#F44336";
    }
}
