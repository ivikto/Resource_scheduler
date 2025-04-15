package org.example.entity.operationsType;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Data
public class OperationType {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private int id;
    private String refKey;
    private String number;
    protected String name;
    private double time;
    private String priority;
    private String nomenclatureName;
    boolean added = false;
}
