package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class Montage extends OperationType {

    public Montage() {
        this.name = "Монтаж";
    }
}
