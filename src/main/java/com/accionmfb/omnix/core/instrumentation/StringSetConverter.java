package com.accionmfb.omnix.core.instrumentation;

import com.accionmfb.omnix.core.commons.StringValues;
import javax.persistence.AttributeConverter;
import org.apache.logging.log4j.util.Strings;

import java.util.*;
import java.util.stream.Collectors;

import static com.accionmfb.omnix.core.commons.StringValues.END_LIST_CHAR;
import static com.accionmfb.omnix.core.commons.StringValues.START_LIST_CHAR;


public class StringSetConverter implements AttributeConverter<Set<String>, String> {

    @Override
    public String convertToDatabaseColumn(Set<String> strings) {
        if(Objects.nonNull(strings) && !strings.isEmpty()){
            strings = strings.stream().map(s -> s.replaceAll("\\[", "").replaceAll("]", "")).collect(Collectors.toSet());
            String concatenatedValues = String.join(StringValues.COMMA, strings);
            String columnList = START_LIST_CHAR.concat(concatenatedValues).concat(END_LIST_CHAR);
            return columnList.replaceAll("\\[", "[").replaceAll("]", Strings.EMPTY).concat("]");
        }
        return Strings.EMPTY;
    }

    @Override
    public Set<String> convertToEntityAttribute(String s) {
        if(Objects.nonNull(s)) {
            s = s.replaceAll("\\[", "[").replaceAll("]", Strings.EMPTY);
            return !s.trim().isEmpty() ? new HashSet<>(Arrays.asList(s.split(StringValues.COMMA))) : new HashSet<>();
        }
        return new HashSet<>();
    }
}
