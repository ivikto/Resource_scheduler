package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.entity.operations_type.OperationType;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Resources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String viewName;
    private String name;
    private String color;
    @OneToMany(mappedBy = "resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OperationType> operationType = new ArrayList<>();
}
