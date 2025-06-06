package io.github.mitsumi.solutions.spring.dbunit.postgres.test;

import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.DbUnitConfiguration;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import io.github.mitsumi.solutions.spring.dbunit.postgres.test.loaders.ReplacementCsvDataSetLoader;
import io.github.mitsumi.solutions.spring.dbunit.postgres.test.operations.DbOperationLookup;
import io.github.mitsumi.solutions.spring.json.accessors.JsonPathAccessorFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.bean.override.mockito.MockitoResetTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@ActiveProfiles("unit-test")
@SpringBootTest(classes = TestSpringDbunitPostgresApplication.class)
@TestExecutionListeners({
    DependencyInjectionTestExecutionListener.class,
    TransactionalTestExecutionListener.class,
    DatabaseUnitTestExecutionListener.class,
    MockitoResetTestExecutionListener.class
})
@DbUnitConfiguration(
    databaseConnection = "dbUnitDatabaseConnection",
    dataSetLoader = ReplacementCsvDataSetLoader.class,
    databaseOperationLookup = DbOperationLookup.class
)
@Import({TestDbUnitConfig.class})
@DatabaseSetup(value = "/test-data/io.github.mitsumi.solutions.spring.dbunit.postgres.test.TestDbUnit/")
@Slf4j
public class TestDbUnit {

    @Autowired
    private DatabaseDataSourceConnectionFactoryBean connectionFactoryBean;

    private final JsonPathAccessorFactory jsonPathAccessorFactory = new JsonPathAccessorFactory();

    @SuppressWarnings("SqlNoDataSourceInspection")
    @SneakyThrows
    @Test
    public void test_dbUnit_loaded() {
        try (var connection = Objects.requireNonNull(connectionFactoryBean.getObject()).getConnection()) {
            var actualUsers = connection.createStatement().executeQuery("select * from tbl_user");

            while (actualUsers.next()) {
                assertThat(
                    actualUsers.getString("user_id"),
                    is("1")
                );
                assertThat(
                    actualUsers.getString("user_key"),
                    is("6c6d35e2-fafa-4358-a831-e1a853b4bc8a")
                );
                assertThat(
                    actualUsers.getString("user_type"),
                    is("USER")
                );
                assertThat(
                    actualUsers.getString("username"),
                    is("test+20250323.001@local.jp")
                );
                assertThat(
                    actualUsers.getString("password"),
                    is("password")
                );

                var actualUserProfile = jsonPathAccessorFactory.create(actualUsers.getString("user_profile"));
                assertThat(
                    actualUserProfile.read("$.family_name"),
                    is("demo family name")
                );
                assertThat(
                    actualUserProfile.read("$.given_name"),
                    is("demo given name")
                );
                assertThat(
                    actualUserProfile.read("$.gender"),
                    is("male")
                );
                assertThat(
                    actualUserProfile.read("$.birthday"),
                    is("1995-02-07")
                );
                assertThat(
                    actualUserProfile.read("$.address.formatted"),
                    is("101-0041\n東京都千代田区神田須田町１丁目７番８号 VORT秋葉原Ⅳ ２Ｆ")
                );
                assertThat(
                    actualUserProfile.read("$.address.postal_code"),
                    is("101-0041")
                );
                assertThat(
                    actualUserProfile.read("$.address.country"),
                    is("日本")
                );
                assertThat(
                    actualUserProfile.read("$.address.region"),
                    is("東京都")
                );
                assertThat(
                    actualUserProfile.read("$.address.locality"),
                    is("千代田区")
                );
                assertThat(
                    actualUserProfile.read("$.address.street_address"),
                    is("神田須田町１丁目７番８号 VORT秋葉原Ⅳ ２Ｆ")
                );

                assertThat(actualUsers.getDate("created_at"), is(notNullValue()));
                assertThat(actualUsers.getDate("updated_at"), is(notNullValue()));

                log.debug("user_id: {}", actualUsers.getString("user_id"));
            }
        }
    }
}
