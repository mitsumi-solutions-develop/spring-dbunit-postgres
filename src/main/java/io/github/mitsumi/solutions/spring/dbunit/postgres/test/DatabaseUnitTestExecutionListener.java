package io.github.mitsumi.solutions.spring.dbunit.postgres.test;

import com.github.springtestdbunit.DatabaseConnections;
import com.github.springtestdbunit.DbUnitRunner;
import com.github.springtestdbunit.DbUnitTestContext;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.dataset.DataSetLoader;
import com.github.springtestdbunit.operation.DatabaseOperationLookup;
import io.github.mitsumi.solutions.spring.dbunit.postgres.test.runners.DatabaseUnitRunner;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * Wrappered DbUnitTestExecutionListener class.
 *
 * @author mitsumi solutions
 */
@NoArgsConstructor
public class DatabaseUnitTestExecutionListener extends DbUnitTestExecutionListener {

    /**
     * DbUnitRunner.
     */
    private static final DbUnitRunner RUNNER = new DatabaseUnitRunner();

    @Override
    public void beforeTestMethod(final TestContext testContext) throws Exception {
        RUNNER.beforeTestMethod(new DbUnitTestContextAdapter(testContext));
    }

    @SuppressWarnings("PMD.CommentRequired")
    private record DbUnitTestContextAdapter(TestContext testContext) implements DbUnitTestContext {

        private static final Method TEST_CLASS;
        private static final Method TEST_INSTANCE;
        private static final Method TEST_METHOD;
        private static final Method TEST_EXCEPTION;
        private static final Method CONTEXT;
        private static final Method GET_ATTRIBUTE;
        private static final Method SET_ATTRIBUTE;

        static {
            try {
                TEST_CLASS = TestContext.class.getMethod("getTestClass");
                TEST_INSTANCE = TestContext.class.getMethod("getTestInstance");
                TEST_METHOD = TestContext.class.getMethod("getTestMethod");
                TEST_EXCEPTION = TestContext.class.getMethod("getTestException");
                CONTEXT = TestContext.class.getMethod("getApplicationContext");
                GET_ATTRIBUTE = TestContext.class.getMethod("getAttribute", String.class);
                SET_ATTRIBUTE = TestContext.class.getMethod("setAttribute", String.class, Object.class);
            } catch (NoSuchMethodException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        public DatabaseConnections getConnections() {
            return (DatabaseConnections) getAttribute(CONNECTION_ATTRIBUTE);
        }

        @Override
        public DataSetLoader getDataSetLoader() {
            return (DataSetLoader) getAttribute(DATA_SET_LOADER_ATTRIBUTE);
        }

        @Override
        public DatabaseOperationLookup getDatbaseOperationLookup() {
            return (DatabaseOperationLookup) getAttribute(DATABASE_OPERATION_LOOKUP_ATTRIBUTE);
        }

        @Override
        public Class<?> getTestClass() {
            return (Class<?>) ReflectionUtils.invokeMethod(TEST_CLASS, this.testContext);
        }

        @Override
        public Method getTestMethod() {
            return (Method) ReflectionUtils.invokeMethod(TEST_METHOD, this.testContext);
        }

        @Override
        public Object getTestInstance() {
            return ReflectionUtils.invokeMethod(TEST_INSTANCE, this.testContext);
        }

        @Override
        public Throwable getTestException() {
            return (Throwable) ReflectionUtils.invokeMethod(TEST_EXCEPTION, this.testContext);
        }

        public ApplicationContext getApplicationContext() {
            return (ApplicationContext) ReflectionUtils.invokeMethod(CONTEXT, this.testContext);
        }

        public Object getAttribute(final String name) {
            return ReflectionUtils.invokeMethod(GET_ATTRIBUTE, this.testContext, name);
        }

        public void setAttribute(final String name, final Object value) {
            ReflectionUtils.invokeMethod(SET_ATTRIBUTE, this.testContext, name, value);
        }

    }
}
