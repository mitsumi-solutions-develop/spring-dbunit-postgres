package io.github.mitsumi.solutions.spring.dbunit.postgres.test.runners;

import com.github.springtestdbunit.DbUnitRunner;
import com.github.springtestdbunit.DbUnitTestContext;
import com.github.springtestdbunit.annotation.DatabaseOperation;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DatabaseSetups;
import com.github.springtestdbunit.annotation.DatabaseTearDown;
import com.github.springtestdbunit.dataset.DataSetModifier;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@NoArgsConstructor
@SuppressWarnings({"PMD.UseExplicitTypes", "PMD.LawOfDemeter", "PMD.CommentRequired"})
public class DatabaseUnitRunner extends DbUnitRunner {

    @Override
    public void beforeTestMethod(final DbUnitTestContext testContext) {
        final var annotations = Annotations.get(testContext);
        setupOrTeardown(testContext, AnnotationAttributes.get(annotations));
    }

    @SneakyThrows
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    private void setupOrTeardown(final DbUnitTestContext testContext,
                                 final Collection<AnnotationAttributes> annotations) {
        final var connections = testContext.getConnections();
        for (final AnnotationAttributes annotation : annotations) {
            final var datasets = loadDataSets(testContext, annotation);
            final var operation = annotation.type();
            final var dbUnitOperation = getDbUnitDatabaseOperation(testContext, operation);
            if (!datasets.isEmpty()) {
                if (log.isDebugEnabled()) {
                    log.debug("Executing Setup of @DatabaseTest using {} on {}", operation, datasets);
                }

                final var connection = connections.get(annotation.connection());
                final var dataSet = new CompositeDataSet(datasets.toArray(new IDataSet[0]));
                dbUnitOperation.execute(connection, dataSet);
                connection.getConnection().close();
                connection.close();
            }
        }
    }

    @SneakyThrows
    private IDataSet getFullDatabaseDataSet(final DbUnitTestContext testContext,
                                            final String name) {
        return testContext.getConnections().get(name).createDataSet();
    }

    private List<IDataSet> loadDataSets(final DbUnitTestContext testContext,
                                        final AnnotationAttributes annotation) {
        final var datasets = new ArrayList<IDataSet>();
        for (final String location : annotation.value()) {
            datasets.add(loadDataset(testContext, location));
        }
        if (datasets.isEmpty()) {
            datasets.add(getFullDatabaseDataSet(testContext, annotation.connection()));
        }
        return datasets;
    }

    @SneakyThrows
    private IDataSet loadDataset(final DbUnitTestContext testContext,
                                 final String dataSetLocation) {
        final var dataSetLoader = testContext.getDataSetLoader();
        if (StringUtils.hasLength(dataSetLocation)) {
            final var dataSet = DataSetModifier.NONE.modify(dataSetLoader.loadDataSet(testContext.getTestClass(), dataSetLocation));
            Assert.notNull(dataSet,
                "Unable to load dataset from \"" + dataSetLocation + "\" using " + dataSetLoader.getClass());
            return dataSet;
        }
        throw new IllegalStateException("Unable to load dataset from \"" + dataSetLocation + "\".");
    }

    private org.dbunit.operation.DatabaseOperation getDbUnitDatabaseOperation(final DbUnitTestContext testContext,
                                                                              final DatabaseOperation operation) {
        final var databaseOperation = testContext.getDatbaseOperationLookup().get(operation);

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

        public AnnotationAttributes(final Annotation annotation) {
            Assert.state((annotation instanceof DatabaseSetup) || (annotation instanceof DatabaseTearDown),
                "Only DatabaseSetup and DatabaseTearDown annotations are supported");
            final var attributes = AnnotationUtils.getAnnotationAttributes(annotation);

            this.type = (DatabaseOperation) attributes.get("type");
            this.value = (String[]) attributes.get("value");
            this.connection = (String) attributes.get("connection");
        }

        public static <T extends Annotation> Collection<AnnotationAttributes> get(final Annotations<T> annotations) {
            final var attributes = new ArrayList<AnnotationAttributes>();
            for (final T annotation : annotations) {
                attributes.add(new AnnotationAttributes(annotation));
            }
            return attributes;
        }

    }

    @Getter
    @Accessors(fluent = true)
    private static class Annotations<T extends Annotation> implements Iterable<T> {

        private final List<T> classAnnotations;

        private final List<T> methodAnnotations;

        private final List<T> allAnnotations;

        public Annotations(final DbUnitTestContext context,
                           final Class<? extends Annotation> container,
                           final Class<T> annotation) {
            this.classAnnotations = getAnnotations(context.getTestClass(), container, annotation);
            this.methodAnnotations = getAnnotations(context.getTestMethod(), container, annotation);
            final var allAnnotations = new ArrayList<T>(this.classAnnotations.size() + this.methodAnnotations.size());

            allAnnotations.addAll(this.classAnnotations);
            allAnnotations.addAll(this.methodAnnotations);

            this.allAnnotations = Collections.unmodifiableList(allAnnotations);
        }

        private List<T> getAnnotations(final AnnotatedElement element,
                                       final Class<? extends Annotation> container,
                                       final Class<T> annotation) {
            final var annotations = new ArrayList<T>();

            addAnnotationToList(annotations, AnnotationUtils.findAnnotation(element, annotation));
            addRepeatableAnnotationsToList(annotations, AnnotationUtils.findAnnotation(element, container));

            return Collections.unmodifiableList(annotations);
        }

        private void addAnnotationToList(final List<T> annotations, final T annotation) {
            if (annotation != null) {
                annotations.add(annotation);
            }
        }

        @SuppressWarnings({"unchecked"})
        private void addRepeatableAnnotationsToList(final List<T> annotations, final Annotation container) {
            if (container != null) {
                final var value = (T[]) AnnotationUtils.getValue(container);
                assert value != null;

                Collections.addAll(annotations, value);
            }
        }

        @Override
        @NotNull
        public Iterator<T> iterator() {
            return this.allAnnotations.iterator();
        }

        @SuppressWarnings("unchecked")
        private static <T extends Annotation> Annotations<T> get(final DbUnitTestContext testContext) {
            return new Annotations<>(testContext, DatabaseSetups.class, (Class<T>) DatabaseSetup.class);
        }
    }
}
