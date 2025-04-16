package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class LaserCleaner extends OperationType {

    public LaserCleaner() {
        this.name = "Лазерная чистка";
    }
}
