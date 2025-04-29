package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class MillingMachine extends OperationKit {

    public MillingMachine() {
        this.name = "Фрезеровка";
        this.color = "#607D8B";
    }
}
