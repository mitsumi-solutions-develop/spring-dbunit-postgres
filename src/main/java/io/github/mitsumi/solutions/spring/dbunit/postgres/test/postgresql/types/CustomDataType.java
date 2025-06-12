package io.github.mitsumi.solutions.spring.dbunit.postgres.test.postgresql.types;

import org.dbunit.dataset.datatype.AbstractDataType;
import org.postgresql.util.PGobject;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

@SuppressWarnings("PMD.CommentRequired")
public class CustomDataType extends AbstractDataType {

    private final String type;

    public CustomDataType(final String type) {
        super(type, Types.OTHER, String.class, false);

        this.type = type;
    }

    @Override
    public Object typeCast(final Object value) {
        return value.toString();
    }

    @Override
    public String getSqlValue(final int column, final ResultSet resultSet) throws SQLException {
        return resultSet.getString(column);
    }

    @Override
    @SuppressWarnings("PMD.UseExplicitTypes")
    public void setSqlValue(final Object value,
                            final int column,
                            final PreparedStatement statement) throws SQLException {
        final var object = new PGobject();

        object.setType(type);

        if (value != null) {
            object.setValue(value.toString());
        }

        statement.setObject(column, object);
    }
}
