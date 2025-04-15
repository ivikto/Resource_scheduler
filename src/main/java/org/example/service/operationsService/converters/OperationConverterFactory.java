package org.example.service.operationsService.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OperationConverterFactory {

    private final List<OperationConverter<?>> converters;

    @SuppressWarnings("unchecked")
    public <T> OperationConverter<T> getConverter(Class<T> type) {
        return (OperationConverter<T>) converters.stream()
                .filter(c -> type.isAssignableFrom(c.getType()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No converter found for type: " + type));
    }
}
