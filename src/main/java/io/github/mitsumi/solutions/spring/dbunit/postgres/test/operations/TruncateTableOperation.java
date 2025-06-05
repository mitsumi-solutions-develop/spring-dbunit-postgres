package io.github.mitsumi.solutions.spring.dbunit.postgres.test.operations;

import lombok.extern.slf4j.Slf4j;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;

@Slf4j
public class TruncateTableOperation extends AbstractDbOperation {

    protected String truncateSqlFormat() {
        return "truncate table %s cascade";
    }

    @Override
    protected String sql(IDatabaseConnection connection, IDataSet dataSet, String tableName) {
        return String.format(truncateSqlFormat(),
            this.getQualifiedName(connection.getSchema(), tableName, connection));
    }

}
