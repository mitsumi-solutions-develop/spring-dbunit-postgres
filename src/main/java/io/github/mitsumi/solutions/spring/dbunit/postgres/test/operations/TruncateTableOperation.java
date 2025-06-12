package io.github.mitsumi.solutions.spring.dbunit.postgres.test.operations;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

@Slf4j
@NoArgsConstructor
@SuppressWarnings("PMD.CommentRequired")
public class TruncateTableOperation extends AbstractDbOperation {

    protected String truncateSqlFormat() {
        return "truncate table %s cascade";
    }

    @Override
    protected String sql(final IDatabaseConnection connection,
                         final IDataSet dataSet,
                         final String tableName) {
        return String.format(truncateSqlFormat(),
            this.getQualifiedName(connection.getSchema(), tableName, connection));
    }

}
