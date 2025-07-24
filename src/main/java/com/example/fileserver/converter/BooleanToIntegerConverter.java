package com.example.fileserver.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false) // Explicitly apply the converter where necessary
public class BooleanToIntegerConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) {
            return null; // Handle null values if applicable
        }
        return attribute ? 1 : 0; // Convert true to 1, false to 0
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null; // Handle null values if necessary
        }
        return dbData.equals(1); // Convert 1 to true, and any other value to false
    }
}
