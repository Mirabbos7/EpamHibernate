package org.example;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.example.facade.GymFacade;
import org.example.testdata.TraineeTestDataCreator;
import org.example.testdata.TrainerTestDataCreator;
import org.example.testdata.TrainingTestDataCreator;
import org.example.testdata.TrainingTypeTestDataCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Properties;
import java.util.function.Supplier;

@Transactional
@Rollback
@SpringJUnitConfig(AbstractSpringIntegrationTest.TestConfig.class)
public abstract class AbstractSpringIntegrationTest {

    @Autowired
    protected TrainerTestDataCreator trainerCreator;
    @Autowired
    protected TraineeTestDataCreator traineeCreator;
    @Autowired
    protected TrainingTestDataCreator trainingCreator;
    @Autowired
    protected TrainingTypeTestDataCreator trainingTypeCreator;
    @Autowired
    protected EntityManager entityManager;

    protected <T> T withDbSync(Supplier<T> action) {
        final var result = action.get();
        entityManager.flush();
        entityManager.clear();
        return result;
    }

    protected void withDbSync(Runnable action) {
        action.run();
        entityManager.flush();
        entityManager.clear();
    }

    @Configuration
    @PropertySource("classpath:application-test.properties")
    @EnableJpaRepositories(basePackages = "org.example.repository")
    @EnableTransactionManagement
    @ComponentScan(basePackages = "org.example")
    @Import(GymFacade.class)
    static class TestConfig {

        @Value("${hibernate.dialect}")
        private String dialect;

        @Value("${hibernate.show_sql}")
        private String showSql;

        @Value("${hibernate.hbm2ddl.auto}")
        private String ddlAuto;

        @Bean
        public DataSource dataSource() {
            return new EmbeddedDatabaseBuilder()
                    .setType(EmbeddedDatabaseType.H2)
                    .build();
        }

        @Bean
        public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(dataSource());
            em.setPackagesToScan("org.example.entity");
            em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
            em.setJpaProperties(hibernateProperties());
            return em;
        }

        @Bean
        public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
            return new JpaTransactionManager(emf);
        }

        private Properties hibernateProperties() {
            Properties props = new Properties();
            props.setProperty("hibernate.dialect", dialect);
            props.setProperty("hibernate.show_sql", showSql);
            props.setProperty("hibernate.hbm2ddl.auto", ddlAuto);
            props.setProperty("hibernate.physical_naming_strategy",
                    "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy");
            return props;
        }
    }
}
