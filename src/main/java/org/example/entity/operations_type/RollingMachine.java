package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class RollingMachine extends OperationKit {

    public RollingMachine() {
        this.name = "Вальцеватель";
        this.color = "#673AB7";
    }
}
