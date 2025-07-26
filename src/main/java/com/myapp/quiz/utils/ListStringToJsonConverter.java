package com.myapp.quiz.utils;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Converter(autoApply = false)
@RequiredArgsConstructor
@Slf4j(topic = "ListToJsonConverter")
public class ListStringToJsonConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> list) {
        log.info("START convertToDatabaseColumn");
        try {
            return list == null ? "[]" : mapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("Error Convert: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String json) {
        log.info("START convertToEntityAttribute");
        try {
            return mapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (IOException e) {
            log.error("Error Convert: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
