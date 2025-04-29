package org.example.entity.operations_type;

import jakarta.persistence.Entity;

@Entity
public class SheetBending extends OperationType {

    public SheetBending() {
        this.name = "Листогиб";
        this.color = "#00BCD4";
    }
}
