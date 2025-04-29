package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class BandSaw extends OperationType {

    public BandSaw() {
        this.name = "Лентопил";
        this.color = "#4CAF50";
    }
}
