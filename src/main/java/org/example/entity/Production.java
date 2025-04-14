package org.example.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Production {

    @Id
    @JsonProperty("Number")
    private String productionId; //Номер
    @JsonProperty("Ref_Key")
    private String refKey;
    @JsonProperty("СостояниеЗаказа_Key")
    private String condition; //Состояние
    @JsonProperty("Приоритет_Key")
    private String priority; //Приоритет
    @JsonProperty("СтруктурнаяЕдиницаОпераций_Key")
    private String manufacturer;//Изготовитель
    @JsonProperty("Date")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    @JsonProperty("ДокументОснование")
    private String foundationDoc;
    @OneToMany
    @JsonProperty("Операции")
    private List<Operation> operations; //Операции

}
