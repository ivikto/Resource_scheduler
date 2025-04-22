package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class BandSaw extends OperationType {

    public BandSaw() {
        this.name = "Лентопил";
        this.color = "#4CAF50";
    }
}
