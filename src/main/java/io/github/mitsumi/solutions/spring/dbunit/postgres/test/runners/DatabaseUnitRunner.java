package io.github.mitsumi.solutions.spring.dbunit.postgres.test.runners;

import com.github.springtestdbunit.DbUnitRunner;
import com.github.springtestdbunit.DbUnitTestContext;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.dataset.DataSetModifier;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.dbunit.dataset.CompositeDataSet;
import org.dbunit.dataset.IDataSet;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.*;

@Slf4j
public class DatabaseUnitRunner extends DbUnitRunner {

    public void beforeTestMethod(DbUnitTestContext testContext) {
        var annotations = Annotations.get(testContext);
        setupOrTeardown(testContext, AnnotationAttributes.get(annotations));
    }

    @SneakyThrows
    private void setupOrTeardown(DbUnitTestContext testContext,
                                 Collection<AnnotationAttributes> annotations) {
        var connections = testContext.getConnections();
        for (AnnotationAttributes annotation : annotations) {
            var datasets = loadDataSets(testContext, annotation);
            var operation = annotation.type();
            var dbUnitOperation = getDbUnitDatabaseOperation(testContext, operation);
            if (!datasets.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("Executing Setup of @DatabaseTest using {} on {}", operation, datasets);
                }

                var connection = connections.get(annotation.connection());
                var dataSet = new CompositeDataSet(datasets.toArray(new IDataSet[0]));
                dbUnitOperation.execute(connection, dataSet);
                connection.getConnection().close();
                connection.close();
            }
        }
    }

    @SneakyThrows
    private IDataSet getFullDatabaseDataSet(DbUnitTestContext testContext, String name) {
        return testContext.getConnections().get(name).createDataSet();
    }

    private List<IDataSet> loadDataSets(DbUnitTestContext testContext, AnnotationAttributes annotation) {
        var datasets = new ArrayList<IDataSet>();
        for (String dataSetLocation : annotation.value()) {
            datasets.add(loadDataset(testContext, dataSetLocation));
        }
        if (datasets.isEmpty()) {
            datasets.add(getFullDatabaseDataSet(testContext, annotation.connection()));
        }
        return datasets;
    }

    @SneakyThrows
    private IDataSet loadDataset(DbUnitTestContext testContext, String dataSetLocation) {
        var dataSetLoader = testContext.getDataSetLoader();
        if (StringUtils.hasLength(dataSetLocation)) {
            var dataSet = DataSetModifier.NONE.modify(dataSetLoader.loadDataSet(testContext.getTestClass(), dataSetLocation));
            Assert.notNull(dataSet,
                "Unable to load dataset from \"" + dataSetLocation + "\" using " + dataSetLoader.getClass());
            return dataSet;
        }
        return null;
    }

    private org.dbunit.operation.DatabaseOperation getDbUnitDatabaseOperation(DbUnitTestContext testContext,
                                                                              DatabaseOperation operation) {
        var databaseOperation = testContext.getDatbaseOperationLookup().get(operation);

        Assert.state(
            databaseOperation != null,
            String.format("The database operation %s is not supported", operation)
        );

        return databaseOperation;
    }

    @Getter
    @Accessors(fluent = true)
    private static class AnnotationAttributes {

        private final DatabaseOperation type;

        private final String[] value;

        private final String connection;

        public AnnotationAttributes(Annotation annotation) {
            Assert.state((annotation instanceof DatabaseSetup) || (annotation instanceof DatabaseTearDown),
                "Only DatabaseSetup and DatabaseTearDown annotations are supported");
            var attributes = AnnotationUtils.getAnnotationAttributes(annotation);

            this.type = (DatabaseOperation) attributes.get("type");
            this.value = (String[]) attributes.get("value");
            this.connection = (String) attributes.get("connection");
        }

        public static <T extends Annotation> Collection<AnnotationAttributes> get(Annotations<T> annotations) {
            var annotationAttributes = new ArrayList<AnnotationAttributes>();
            for (T annotation : annotations) {
                annotationAttributes.add(new AnnotationAttributes(annotation));
            }
            return annotationAttributes;
        }

    }

    @Getter
    @Accessors(fluent = true)
    private static class Annotations<T extends Annotation> implements Iterable<T> {

        private final List<T> classAnnotations;

        private final List<T> methodAnnotations;

        private final List<T> allAnnotations;

        public Annotations(DbUnitTestContext context, Class<? extends Annotation> container, Class<T> annotation) {
            this.classAnnotations = getAnnotations(context.getTestClass(), container, annotation);
            this.methodAnnotations = getAnnotations(context.getTestMethod(), container, annotation);
            var allAnnotations = new ArrayList<T>(this.classAnnotations.size() + this.methodAnnotations.size());

            allAnnotations.addAll(this.classAnnotations);
            allAnnotations.addAll(this.methodAnnotations);

            this.allAnnotations = Collections.unmodifiableList(allAnnotations);
        }

        private List<T> getAnnotations(AnnotatedElement element, Class<? extends Annotation> container,
                                       Class<T> annotation) {
            var annotations = new ArrayList<T>();

            addAnnotationToList(annotations, AnnotationUtils.findAnnotation(element, annotation));
            addRepeatableAnnotationsToList(annotations, AnnotationUtils.findAnnotation(element, container));

            return Collections.unmodifiableList(annotations);
        }

        private void addAnnotationToList(List<T> annotations, T annotation) {
            if (annotation != null) {
                annotations.add(annotation);
            }
        }

        @SuppressWarnings("unchecked")
        private void addRepeatableAnnotationsToList(List<T> annotations, Annotation container) {
            if (container != null) {
                var value = (T[]) AnnotationUtils.getValue(container);
                assert value != null;

                Collections.addAll(annotations, value);
            }
        }

        @NotNull
        public Iterator<T> iterator() {
            return this.allAnnotations.iterator();
        }

        @SuppressWarnings("unchecked")
        private static <T extends Annotation> Annotations<T> get(DbUnitTestContext testContext) {
            return new Annotations<>(testContext, DatabaseSetups.class, (Class<T>) DatabaseSetup.class);
        }
    }
}
