package io.github.mitsumi.solutions.spring.dbunit.postgres.test.operations;

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
public abstract class AbstractDbOperation extends AbstractOperation {

    protected abstract String sql(IDatabaseConnection connection, IDataSet dataSet, String tableName) throws DataSetException;

    @Override
    public void execute(IDatabaseConnection connection, IDataSet dataSet) throws SQLException {
        log.debug("execute(connection={}, dataSet={}) - start", connection, dataSet);

        var databaseDataSet = connection.createDataSet();
        var databaseConfig = connection.getConfig();
        var statementFactory = (IStatementFactory) databaseConfig.getProperty("http://www.dbunit.org/properties/statementFactory");
        var statement = statementFactory.createBatchStatement(connection);

        try {
            int count = 0;
            var tableNames = new Stack<String>();
            var tablesSeen = new HashSet<String>();
            var iterator = dataSet.iterator();

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

                var databaseMetaData = databaseDataSet.getTableMetaData(tableName);

                tableName = databaseMetaData.getTableName();

                var sql = sql(connection, dataSet, tableName);

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
        } catch (Exception e) {
            log.warn(e.getMessage());
        } finally {
            statement.close();
        }
    }
}
