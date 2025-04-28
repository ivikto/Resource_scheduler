package org.example.entity.operations_type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.entity.Resources;

@Builder
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Data
@AllArgsConstructor
@NoArgsConstructor
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resource_id")
    @JsonIgnore
    private Resources resource;

    private boolean isEdited = false;
    private boolean markForDelete = false;
    private boolean isFinish = false;
}
