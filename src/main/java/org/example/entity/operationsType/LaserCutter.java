package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class LaserCutter extends OperationType {

    public LaserCutter() {
        this.name = "Лазерная резка";
    }
}
