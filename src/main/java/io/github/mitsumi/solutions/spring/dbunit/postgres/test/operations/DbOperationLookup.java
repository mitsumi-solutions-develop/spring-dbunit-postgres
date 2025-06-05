package io.github.mitsumi.solutions.spring.dbunit.postgres.test.operations;

import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.operation.DefaultDatabaseOperationLookup;
import org.dbunit.operation.CompositeOperation;

import java.util.HashMap;
import java.util.Map;

public class DbOperationLookup extends DefaultDatabaseOperationLookup {
    private static final Map<DatabaseOperation, org.dbunit.operation.DatabaseOperation> OPERATION_LOOKUP;

    private final static org.dbunit.operation.DatabaseOperation TRUNCATE_TABLE = new TruncateTableOperation();
    private final static org.dbunit.operation.DatabaseOperation RESET_SEQUENCE = new SequenceOperation();
    private static final org.dbunit.operation.DatabaseOperation CLEAN_INSERT = new CompositeOperation(
        new org.dbunit.operation.DatabaseOperation[]{
            TRUNCATE_TABLE, org.dbunit.operation.DatabaseOperation.INSERT, RESET_SEQUENCE
        }
    );

    static {
        OPERATION_LOOKUP = new HashMap<>();
        OPERATION_LOOKUP.put(DatabaseOperation.UPDATE, org.dbunit.operation.DatabaseOperation.UPDATE);
        OPERATION_LOOKUP.put(DatabaseOperation.INSERT, org.dbunit.operation.DatabaseOperation.INSERT);
        OPERATION_LOOKUP.put(DatabaseOperation.REFRESH, org.dbunit.operation.DatabaseOperation.REFRESH);
        OPERATION_LOOKUP.put(DatabaseOperation.DELETE, org.dbunit.operation.DatabaseOperation.DELETE);
        OPERATION_LOOKUP.put(DatabaseOperation.DELETE_ALL, TRUNCATE_TABLE);
        OPERATION_LOOKUP.put(DatabaseOperation.TRUNCATE_TABLE, TRUNCATE_TABLE);
        OPERATION_LOOKUP.put(DatabaseOperation.CLEAN_INSERT, CLEAN_INSERT);
    }

    @Override
    public org.dbunit.operation.DatabaseOperation get(DatabaseOperation operation) {
        return OPERATION_LOOKUP.get(operation);
    }
}
