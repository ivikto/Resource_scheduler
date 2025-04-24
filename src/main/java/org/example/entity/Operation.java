package org.example.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Operation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JsonProperty("Ref_Key")
    private String operationRefKey;

    @JsonProperty("Операция_Key")
    private String operationKey;

    private String nomenclature;

    @JsonProperty("Нормочасы")
    private double operationTime;
    @ManyToOne
    @JoinColumn(name = "production_ref_key")
    @JsonBackReference
    Production production;
    private boolean isFinish = false;



}
