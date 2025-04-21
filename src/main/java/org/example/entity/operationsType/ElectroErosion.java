package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class ElectroErosion extends OperationType{
    public ElectroErosion() {
        this.name = "Электро-эрозия";
        this.color = "#9E9E9E";
    }
}
