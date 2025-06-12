package io.github.mitsumi.solutions.spring.dbunit.postgres.test.postgresql;

import io.github.mitsumi.solutions.spring.dbunit.postgres.test.postgresql.types.DataTypeEnum;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;

@AllArgsConstructor
@SuppressWarnings("PMD.CommentRequired")
public class PostgreSQLDataTypeFactory extends PostgresqlDataTypeFactory {
    @Override
    public DataType createDataType(final int sqlType, final String sqlTypeName) throws DataTypeException {
        return DataTypeEnum.typeOf(sqlTypeName)
            .orElseGet(() -> defaultDataType(sqlType, sqlTypeName));
    }

    @SneakyThrows
    protected DataType defaultDataType(final int sqlType, final String sqlTypeName) {
        return super.createDataType(sqlType, sqlTypeName);
    }

}
