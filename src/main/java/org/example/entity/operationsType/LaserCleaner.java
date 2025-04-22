package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class LaserCleaner extends OperationType {

    public LaserCleaner() {
        this.name = "Лазерная чистка";
        this.color = "#9C27B0";
    }
}
