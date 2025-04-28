package org.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class Nomenclature {

    @Id
    @JsonProperty("Ref_Key")
    private String refKey;
    @JsonProperty("Code")
    private String code;
    @JsonProperty("Description")
    private String description; // Наименование
}
