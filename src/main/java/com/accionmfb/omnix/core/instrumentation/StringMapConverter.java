package com.accionmfb.omnix.core.instrumentation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import javax.persistence.AttributeConverter;
import java.util.HashMap;
import java.util.Map;

public class StringMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private final static ObjectMapper OBJECT_MAPPER = getObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        try {
            return OBJECT_MAPPER.writeValueAsString(stringObjectMap);
        } catch (JsonProcessingException ignored) {
            return "{}";
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        try{
            return OBJECT_MAPPER.readValue(s.trim(), new TypeReference<Map<String, Object>>() {
            });
        }catch (Exception ignored){
            return new HashMap<>();
        }
    }

    private static ObjectMapper getObjectMapper(){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }
}
