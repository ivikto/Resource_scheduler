package org.example.entity.operationsType;

import jakarta.persistence.Entity;

@Entity
public class SheetBending extends OperationType {

    public SheetBending() {
        this.name = "Листогиб";
    }
}
