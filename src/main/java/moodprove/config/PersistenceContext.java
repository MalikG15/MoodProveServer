package moodprove.config;

import java.sql.SQLException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

/**
 * @author nschuste
 * @version 1.0.0
 * @since Feb 5, 2016
 */
@Configuration
class PersistenceContext {

  @Bean
  public DataSource dataSource(final Environment env) throws IllegalStateException, SQLException {
    final MysqlDataSource dts = new MysqlDataSource();
    dts.setUser(env.getRequiredProperty("db.username"));
    dts.setPassword(env.getRequiredProperty("db.password"));
    dts.setURL(env.getRequiredProperty("db.url"));
    return dts;
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory(final DataSource dataSource,
      final Environment env) {
    final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean =
        new LocalContainerEntityManagerFactoryBean();
    entityManagerFactoryBean.setDataSource(dataSource);
    entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

    final Properties jpaProperties = new Properties();

    // Configures the used database dialect. This allows Hibernate to create SQL
    // that is optimized for the used database.
    jpaProperties.put("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));

    // Specifies the action that is invoked to the database when the Hibernate
    // SessionFactory is created or closed.
    jpaProperties.put("hibernate.hbm2ddl.auto", env.getRequiredProperty("hibernate.hbm2ddl.auto"));

    // Configures the naming strategy that is used when Hibernate creates
    // new database objects and schema elements
    // jpaProperties.put("hibernate.ejb.naming_strategy",
    // env.getRequiredProperty("hibernate.ejb.naming_strategy"));

    // If the value of this property is true, Hibernate writes all SQL
    // statements to the console.
    jpaProperties.put("hibernate.show_sql", env.getRequiredProperty("hibernate.show_sql"));

    // If the value of this property is true, Hibernate will format the SQL
    // that is written to the console.
    jpaProperties.put("hibernate.format_sql", env.getRequiredProperty("hibernate.format_sql"));

    entityManagerFactoryBean.setJpaProperties(jpaProperties);
    entityManagerFactoryBean.setPackagesToScan("moodprove");
    return entityManagerFactoryBean;
  }

  @Bean
  JpaTransactionManager transactionManager(final EntityManagerFactory entityManagerFactory) {
    final JpaTransactionManager transactionManager = new JpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactory);
    return transactionManager;
  }
}

