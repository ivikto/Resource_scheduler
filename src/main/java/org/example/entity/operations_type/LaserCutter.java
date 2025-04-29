package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class LaserCutter extends OperationKit {

    public LaserCutter() {
        this.name = "Лазерная резка";
        this.color = "#FF5722";
    }
}
