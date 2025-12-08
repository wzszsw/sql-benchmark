package com.easyquery.benchmark;

import com.easyquery.benchmark.entity.User;
import com.easyquery.benchmark.hibernate.HibernateUser;
import com.easyquery.benchmark.hibernate.HibernateUtil;
import com.easy.query.core.api.client.EasyQueryClient;
import com.easy.query.core.basic.jdbc.tx.Transaction;
import com.easy.query.core.bootstrapper.EasyQueryBootstrapper;
import com.easy.query.h2.config.H2DatabaseConfiguration;
import com.easy.query.api.proxy.client.DefaultEasyEntityQuery;
import jakarta.persistence.EntityManager;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.jooq.impl.DSL.*;


@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 5, time = 3)
@Measurement(iterations = 10, time = 3)
@Fork(3)
@Threads(1)
public class InsertBenchmark {

    private DefaultEasyEntityQuery easyEntityQuery;
    private DSLContext jooqDsl;
    private EntityManager entityManager;

    @Setup(Level.Trial)
    public void setup() {
        EasyQueryClient easyQueryClient = EasyQueryBootstrapper.defaultBuilderConfiguration()
                .setDefaultDataSource(DatabaseInitializer.getDataSource())
                .useDatabaseConfigure(new H2DatabaseConfiguration())
                .build();
        easyEntityQuery = new DefaultEasyEntityQuery(easyQueryClient);

        // 初始化 JOOQ
        jooqDsl = DSL.using(DatabaseInitializer.getDataSource(), SQLDialect.H2);

        // 初始化 Hibernate
        entityManager = HibernateUtil.createEntityManager();
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
        DatabaseInitializer.clearData();
    }

    @Benchmark
    public void easyQueryInsertSingle() {
        try (Transaction transaction = easyEntityQuery.beginTransaction()) {
            String id = UUID.randomUUID().toString();
            User user = new User(id, "user_" + id, "user@example.com", 25, "1234567890", "Test Address");
            easyEntityQuery.insertable(user).executeRows();
            transaction.commit();
        }
    }

    @Benchmark
    public void jooqInsertSingle() {
        String id = UUID.randomUUID().toString();
        com.easyquery.benchmark.jooq.JooqUser user = new com.easyquery.benchmark.jooq.JooqUser(
                id, "user_" + id, "user@example.com", 25, "1234567890", "Test Address");
        
        jooqDsl.transaction(configuration -> {
            DSL.using(configuration)
                    .insertInto(table("t_user"))
                    .columns(
                            field("id"), field("username"), field("email"),
                            field("age"), field("phone"), field("address")
                    )
                    .values(user.getId(), user.getUsername(), user.getEmail(),
                            user.getAge(), user.getPhone(), user.getAddress())
                    .execute();
        });
    }


    @Benchmark
    public void easyQueryInsertBatch1000() {
        try (Transaction transaction = easyEntityQuery.beginTransaction()) {
            List<User> users = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                String id = UUID.randomUUID().toString();
                User user = new User(id, "user_" + id, "user@example.com", 25 + (i % 50), "1234567890", "Test Address");
                users.add(user);
            }
            easyEntityQuery.insertable(users).executeRows();
            transaction.commit();
        }
    }


    @Benchmark
    public void jooqInsertBatch1000() {
        List<com.easyquery.benchmark.jooq.JooqUser> users = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String id = UUID.randomUUID().toString();
            com.easyquery.benchmark.jooq.JooqUser user = new com.easyquery.benchmark.jooq.JooqUser(
                    id, "user_" + id, "user@example.com", 25 + (i % 50), "1234567890", "Test Address");
            users.add(user);
        }
        
        jooqDsl.transaction(configuration -> {
            var batchQuery = DSL.using(configuration).batch(
                    DSL.using(configuration).insertInto(table("t_user"))
                            .columns(
                                    field("id"), field("username"), field("email"),
                                    field("age"), field("phone"), field("address")
                            )
                            .values((String) null, null, null, null, null, null)
            );

            for (com.easyquery.benchmark.jooq.JooqUser user : users) {
                batchQuery.bind(
                        user.getId(), user.getUsername(), user.getEmail(),
                        user.getAge(), user.getPhone(), user.getAddress()
                );
            }

            batchQuery.execute();
        });
    }

    @Benchmark
    public void hibernateInsertSingle() {
        entityManager.getTransaction().begin();
        try {
            String id = UUID.randomUUID().toString();
            HibernateUser user = new HibernateUser(id, "user_" + id, "user@example.com", 25, "1234567890", "Test Address");
            entityManager.persist(user);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @Benchmark
    public void hibernateInsertBatch1000() {
        List<HibernateUser> users = new ArrayList<>();

        entityManager.getTransaction().begin();
        try {
            for (int i = 0; i < 1000; i++) {
                String id = UUID.randomUUID().toString();
                HibernateUser user = new HibernateUser(id, "user_" + id, "user@example.com", 25 + (i % 50), "1234567890", "Test Address");
                users.add(user);
            }

            for (HibernateUser user : users) {
                entityManager.persist(user);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw e;
        }
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
