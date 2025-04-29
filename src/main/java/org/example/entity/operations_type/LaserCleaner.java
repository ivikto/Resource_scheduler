package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class LaserCleaner extends OperationKit {

    public LaserCleaner() {
        this.name = "Лазерная чистка";
        this.color = "#9C27B0";
    }
}
