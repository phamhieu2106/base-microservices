package org.example.customerservice.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import org.example.customerservice.domain.model.IdentityModel;

public class ProofAttributeConverter implements AttributeConverter<IdentityModel, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(IdentityModel identityType) {
        try {
            return objectMapper.writeValueAsString(identityType);
        } catch (JsonProcessingException jpe) {
            System.out.println("Cannot convert IdentityType into JSON");
            return null;
        }
    }

    @Override
    public IdentityModel convertToEntityAttribute(String value) {
        if (value == null) {
            return null;
        }

        try {
            return objectMapper.readValue(value, IdentityModel.class);
        } catch (Exception e) {
            System.out.println("Cannot convert JSON into IdentityType");
            return null;
        }
    }
}
