package io.github.mitsumi.solutions.spring.dbunit.postgres.test.postgresql.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.dbunit.dataset.datatype.DataType;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
@Accessors(fluent = true)
public enum DataTypeEnum {

    JSONB("jsonb", s -> s.equalsIgnoreCase("jsonb")),
    UUID("uuid", s -> s.equalsIgnoreCase("uuid")),
    ENUM("enum", s -> s.toLowerCase().endsWith("_enum"));

    private final String type;
    private final Predicate<String> predicate;

    public static Optional<DataType> typeOf(String type) {
        return Stream.of(values())
            .filter(dataType -> dataType.predicate.test(type))
            .map(__ -> new CustomDataType(type))
            .map(DataType.class::cast)
            .findFirst();
    }
}
