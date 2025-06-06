package io.github.mitsumi.solutions.spring.dbunit.postgres.test.postgresql;

import io.github.mitsumi.solutions.spring.dbunit.postgres.test.postgresql.types.DataTypeEnum;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@AllArgsConstructor
public class PostgreSQLDataTypeFactory extends PostgresqlDataTypeFactory {
    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        return DataTypeEnum.typeOf(sqlTypeName)
            .orElseGet(() -> defaultDataType(sqlType, sqlTypeName));
    }

    @SneakyThrows
    protected DataType defaultDataType(int sqlType, String sqlTypeName) {
        return super.createDataType(sqlType, sqlTypeName);
    }

}
