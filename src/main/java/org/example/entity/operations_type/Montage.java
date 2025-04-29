package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class Montage extends OperationType {

    public Montage() {
        this.name = "Монтаж";
        this.color = "#795548";
    }
}
