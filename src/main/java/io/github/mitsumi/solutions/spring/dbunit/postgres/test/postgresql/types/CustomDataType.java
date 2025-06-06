package io.github.mitsumi.solutions.spring.dbunit.postgres.test.postgresql.types;

import org.dbunit.dataset.datatype.AbstractDataType;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;


public class CustomDataType extends AbstractDataType {

    private final String type;

    public CustomDataType(String type) {
        super(type, Types.OTHER, String.class, false);

        this.type = type;
    }

    @Override
    public Object typeCast(Object value) {
        return value.toString();
    }

    @Override
    public String getSqlValue(int column, ResultSet resultSet) throws SQLException {
        return resultSet.getString(column);
    }

    @Override
    public void setSqlValue(Object value, int column, PreparedStatement statement) throws SQLException {
        var object = new PGobject();

        object.setType(type);

        if (value != null) {
            object.setValue(value.toString());
        }

        statement.setObject(column, object);
    }
}
