package io.github.mitsumi.solutions.spring.dbunit.postgres.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "io.github.mitsumi.solutions")
public class TestSpringDbunitPostgresApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestSpringDbunitPostgresApplication.class, args);
    }
}
