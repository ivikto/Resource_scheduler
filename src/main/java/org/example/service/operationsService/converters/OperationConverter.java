package org.example.service.operationsService.converters;

import org.example.entity.Production;

import java.util.List;

public interface OperationConverter<T> {
    T convert(Production production);
    List<String> getSupportedNomenclatures();
    Class<T> getType();
}
