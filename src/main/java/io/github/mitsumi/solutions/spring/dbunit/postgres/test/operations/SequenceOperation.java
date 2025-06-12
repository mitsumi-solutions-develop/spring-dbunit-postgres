package io.github.mitsumi.solutions.spring.dbunit.postgres.test.operations;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;

@Slf4j
@NoArgsConstructor
@SuppressWarnings("PMD.CommentRequired")
public class SequenceOperation extends AbstractDbOperation {

    private String sqlFormat() {
        return """
                WITH table_info as (
                  SELECT
                    c.table_name,
                    c.column_name
                  FROM
                    information_schema.table_constraints tc
                    JOIN information_schema.constraint_column_usage AS ccu USING (constraint_schema, constraint_name)
                    JOIN information_schema.columns AS c ON c.table_schema = tc.constraint_schema
                    AND tc.table_name = c.table_name
                    AND ccu.column_name = c.column_name
                  WHERE
                    constraint_type = 'PRIMARY KEY'
                    and tc.table_name = %s
                )
                SELECT
                  setval(
                    pg_get_serial_sequence(table_info.table_name, table_info.column_name),
                    %d
                  )
                FROM
                  table_info;
                """;
    }

    @Override
    @SuppressWarnings("PMD.UseExplicitTypes")
    protected String sql(final IDatabaseConnection connection,
                         final IDataSet dataSet,
                         final String tableName) throws DataSetException {
        final var rowCount = dataSet.getTable(tableName).getRowCount();
        return rowCount == 0 ? null : String.format(sqlFormat(), tableName, rowCount);
    }
}
