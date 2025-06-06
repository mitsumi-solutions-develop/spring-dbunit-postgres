package io.github.mitsumi.solutions.spring.dbunit.postgres.test;

import com.github.springtestdbunit.DatabaseConnections;
import com.github.springtestdbunit.DbUnitRunner;
import com.github.springtestdbunit.DbUnitTestContext;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.dataset.DataSetLoader;
import com.github.springtestdbunit.operation.DatabaseOperationLookup;
import io.github.mitsumi.solutions.spring.dbunit.postgres.test.runners.DatabaseUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContext;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class DatabaseUnitTestExecutionListener extends DbUnitTestExecutionListener {

    private static final DbUnitRunner runner = new DatabaseUnitRunner();

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        runner.beforeTestMethod(new DbUnitTestContextAdapter(testContext));
    }

    private record DbUnitTestContextAdapter(TestContext testContext) implements DbUnitTestContext {

            private static final Method GET_TEST_CLASS;
            private static final Method GET_TEST_INSTANCE;
            private static final Method GET_TEST_METHOD;
            private static final Method GET_TEST_EXCEPTION;
            private static final Method GET_APPLICATION_CONTEXT;
            private static final Method GET_ATTRIBUTE;
            private static final Method SET_ATTRIBUTE;

            static {
                try {
                    GET_TEST_CLASS = TestContext.class.getMethod("getTestClass");
                    GET_TEST_INSTANCE = TestContext.class.getMethod("getTestInstance");
                    GET_TEST_METHOD = TestContext.class.getMethod("getTestMethod");
                    GET_TEST_EXCEPTION = TestContext.class.getMethod("getTestException");
                    GET_APPLICATION_CONTEXT = TestContext.class.getMethod("getApplicationContext");
                    GET_ATTRIBUTE = TestContext.class.getMethod("getAttribute", String.class);
                    SET_ATTRIBUTE = TestContext.class.getMethod("setAttribute", String.class, Object.class);
                } catch (Exception ex) {
                    throw new IllegalStateException(ex);
                }
            }

        public DatabaseConnections getConnections() {
                return (DatabaseConnections) getAttribute(CONNECTION_ATTRIBUTE);
            }

            public DataSetLoader getDataSetLoader() {
                return (DataSetLoader) getAttribute(DATA_SET_LOADER_ATTRIBUTE);
            }

            public DatabaseOperationLookup getDatbaseOperationLookup() {
                return (DatabaseOperationLookup) getAttribute(DATABASE_OPERATION_LOOKUP_ATTRIBUTE);
            }

            public Class<?> getTestClass() {
                return (Class<?>) ReflectionUtils.invokeMethod(GET_TEST_CLASS, this.testContext);
            }

            public Method getTestMethod() {
                return (Method) ReflectionUtils.invokeMethod(GET_TEST_METHOD, this.testContext);
            }

            public Object getTestInstance() {
                return ReflectionUtils.invokeMethod(GET_TEST_INSTANCE, this.testContext);
            }

            public Throwable getTestException() {
                return (Throwable) ReflectionUtils.invokeMethod(GET_TEST_EXCEPTION, this.testContext);
            }

            public ApplicationContext getApplicationContext() {
                return (ApplicationContext) ReflectionUtils.invokeMethod(GET_APPLICATION_CONTEXT, this.testContext);
            }

            public Object getAttribute(String name) {
                return ReflectionUtils.invokeMethod(GET_ATTRIBUTE, this.testContext, name);
            }

            public void setAttribute(String name, Object value) {
                ReflectionUtils.invokeMethod(SET_ATTRIBUTE, this.testContext, name, value);
            }

        }
}
