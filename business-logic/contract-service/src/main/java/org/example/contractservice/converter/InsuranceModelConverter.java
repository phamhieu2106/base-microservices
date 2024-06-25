package org.example.contractservice.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;

import java.util.List;

public class InsuranceModelConverter implements AttributeConverter<List<InsuranceModelConverter>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<InsuranceModelConverter> m) {
        try {
            return objectMapper.writeValueAsString(m);
        } catch (JsonProcessingException jpe) {
            System.out.println("Cannot convert Object into JSON");
            return null;
        }
    }

    @Override
    public List<InsuranceModelConverter> convertToEntityAttribute(String s) {
        if (s == null) {
            return null;
        }
        try {
            return objectMapper.readValue(s, new TypeReference<>() {
            });
        } catch (Exception e) {
            System.out.println("Cannot convert JSON into List");
            return null;
        }
    }

}
