package io.github.mitsumi.solutions.spring.dbunit.postgres.test.operations;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.statement.IStatementFactory;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.AbstractOperation;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.Stack;

@Slf4j
@NoArgsConstructor
@SuppressWarnings({"PMD.UseExplicitTypes", "PMD.LawOfDemeter", "PMD.CommentRequired"})
public abstract class AbstractDbOperation extends AbstractOperation {

    protected abstract String sql(IDatabaseConnection connection, IDataSet dataSet, String tableName) throws DataSetException;

    @Override
    public void execute(final IDatabaseConnection connection, final IDataSet dataSet)
        throws SQLException, DataSetException {

        log.debug("execute(connection={}, dataSet={}) - start", connection, dataSet);

        final var databaseDataSet = connection.createDataSet();
        final var databaseConfig = connection.getConfig();
        final var statementFactory = (IStatementFactory) databaseConfig.getProperty("http://www.dbunit.org/properties/statementFactory");
        final var statement = statementFactory.createBatchStatement(connection);

        try {
            int count = 0;
            final var tableNames = new Stack<String>();
            final var tablesSeen = new HashSet<String>();
            final var iterator = dataSet.iterator();

            String tableName;
            while (iterator.next()) {
                tableName = iterator.getTableMetaData().getTableName();
                if (!tablesSeen.contains(tableName)) {
                    tableNames.push(tableName);
                    tablesSeen.add(tableName);
                }
            }

            for (; !tableNames.isEmpty(); ++count) {
                tableName = tableNames.pop();

                final var databaseMetaData = databaseDataSet.getTableMetaData(tableName);

                tableName = databaseMetaData.getTableName();

                final var sql = sql(connection, dataSet, tableName);

                if (StringUtils.isNotEmpty(sql)) {
                    statement.addBatch(sql);

                    if (log.isDebugEnabled()) {
                        log.debug("Added SQL: {}", sql);
                    }
                }
            }
            if (count > 0) {
                statement.executeBatch();
                statement.clearBatch();
            }
        } finally {
            statement.close();
        }
    }
}
