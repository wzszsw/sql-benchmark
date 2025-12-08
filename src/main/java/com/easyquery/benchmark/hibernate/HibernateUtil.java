package com.easyquery.benchmark.hibernate;

import com.easyquery.benchmark.DatabaseInitializer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

/**
 * Hibernate configuration utility for benchmark tests
 */
public class HibernateUtil {
    
    private static volatile EntityManagerFactory entityManagerFactory;
    
    /**
     * Get or create EntityManagerFactory
     */
    public static EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null) {
            synchronized (HibernateUtil.class) {
                if (entityManagerFactory == null) {
                    entityManagerFactory = buildEntityManagerFactory();
                }
            }
        }
        return entityManagerFactory;
    }
    
    /**
     * Build EntityManagerFactory with Hibernate configuration
     */
    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            Configuration configuration = new Configuration();
            
            // Configure properties
            Properties properties = new Properties();
            properties.put("hibernate.connection.datasource", DatabaseInitializer.getDataSource());
            properties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            properties.put("hibernate.show_sql", "false");
            properties.put("hibernate.format_sql", "false");
            properties.put("hibernate.hbm2ddl.auto", "none");
            properties.put("hibernate.jdbc.batch_size", "1000");
            properties.put("hibernate.order_inserts", "true");
            properties.put("hibernate.order_updates", "true");
            properties.put("hibernate.jdbc.batch_versioned_data", "true");

//            // Disable transaction management for benchmark fairness
//            properties.put("hibernate.connection.autocommit", "true");
//            properties.put("hibernate.allow_update_outside_transaction", "true");
            
            configuration.setProperties(properties);
            
            // Register entity classes
            configuration.addAnnotatedClass(HibernateUser.class);
            configuration.addAnnotatedClass(HibernateOrder.class);
            
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();
            
            SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            return sessionFactory;
            
        } catch (Throwable ex) {
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    /**
     * Create a new EntityManager
     */
    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    /**
     * Shutdown EntityManagerFactory
     */
    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}



