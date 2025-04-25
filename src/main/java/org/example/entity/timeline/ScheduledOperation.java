package org.example.entity.timeline;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ScheduledOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String resourceId;

    @Column(columnDefinition = "TEXT")  // Для хранения JSON
    private String operations;  // Строка с JSON (например, "{"task1": [...]}")

    @Column(name = "operation_date")
    private LocalDateTime operationDate;
}
