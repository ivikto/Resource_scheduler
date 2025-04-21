package org.example.entity.operationsType;

import jakarta.persistence.*;
import lombok.Data;
import org.example.entity.Resources;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
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
    private boolean inTimeLine = false;
    protected String color;
}
