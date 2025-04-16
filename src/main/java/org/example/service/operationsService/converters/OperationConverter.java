package org.example.service.operationsService.converters;

import org.example.entity.Production;
import org.example.service.Request;

import java.util.List;

public interface OperationConverter<T> {

    T convert(Production production);
    List<String> getSupportedNomenclatures();
    Class<T> getType();
    String getNumName(Production production);
}
