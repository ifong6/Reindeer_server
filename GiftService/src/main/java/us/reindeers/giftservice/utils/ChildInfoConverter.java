package us.reindeers.giftservice.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import us.reindeers.giftservice.domain.entity.ChildInfo;

@Converter(autoApply = true)
public class ChildInfoConverter implements AttributeConverter<ChildInfo, String> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(ChildInfo attribute) {
        if (attribute == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting ChildInfo to JSON", e);
        }
    }

    @Override
    public ChildInfo convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, ChildInfo.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Error converting JSON to ChildInfo", e);
        }
    }
}