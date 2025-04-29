package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class GrindingMachine extends OperationType {

    public GrindingMachine() {
        this.name = "Шлифовальный станок";
        this.color = "#FF9800";
    }
}
