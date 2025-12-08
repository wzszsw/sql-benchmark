package com.easyquery.benchmark.hibernate;

import com.easyquery.benchmark.DatabaseInitializer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.Properties;

public class HibernateUtil {
    
    private static volatile EntityManagerFactory entityManagerFactory;
    
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
    
    private static EntityManagerFactory buildEntityManagerFactory() {
        try {
            Configuration configuration = new Configuration();
            
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
            
            properties.put("hibernate.jdbc.fetch_size", "100");
            properties.put("hibernate.default_batch_fetch_size", "16");
            
            properties.put("hibernate.cache.use_second_level_cache", "false");
            properties.put("hibernate.cache.use_query_cache", "false");
            
            configuration.setProperties(properties);
            
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
    
    public static EntityManager createEntityManager() {
        return getEntityManagerFactory().createEntityManager();
    }
    
    public static void shutdown() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}



