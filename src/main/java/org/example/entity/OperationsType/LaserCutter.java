package org.example.entity.operationsType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class LaserCutter {

    @Id
    private int id;
    private String refKey;
    private String number;
    private String name = "Лазерная резка";
    private double time;
    private String priority;
}
