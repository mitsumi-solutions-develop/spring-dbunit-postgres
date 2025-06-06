package io.github.mitsumi.solutions.spring.dbunit.postgres.test;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import io.github.mitsumi.solutions.spring.dbunit.postgres.test.postgresql.PostgreSQLDataTypeFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@TestConfiguration
public class TestDbUnitConfig {

    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        var bean = new DatabaseConfigBean();

        bean.setAllowEmptyFields(true);
        bean.setEscapePattern("\"?\"");
        bean.setDatatypeFactory(new PostgreSQLDataTypeFactory());

        return bean;
    }

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(DatabaseConfigBean dbUnitDatabaseConfig,
                                                                            DataSource dataSource) {
        var bean = new DatabaseDataSourceConnectionFactoryBean(dataSource);

        bean.setDatabaseConfig(dbUnitDatabaseConfig);
        bean.setSchema("public");

        return bean;
    }

}
