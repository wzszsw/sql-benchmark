package com.easyquery.benchmark;

import com.easyquery.benchmark.entity.Order;
import com.easyquery.benchmark.entity.User;
import com.easyquery.benchmark.hibernate.HibernateUser;
import com.easyquery.benchmark.hibernate.HibernateUtil;
import com.easy.query.api.proxy.client.DefaultEasyEntityQuery;
import com.easy.query.core.api.client.EasyQueryClient;
import com.easy.query.core.bootstrapper.EasyQueryBootstrapper;
import com.easy.query.h2.config.H2DatabaseConfiguration;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.openjdk.jmh.annotations.*;

import java.math.BigDecimal;
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
public class
ComplexQueryBenchmark {

    private DefaultEasyEntityQuery easyEntityQuery;
    private DSLContext jooqDsl;
    private EntityManager entityManager;

    @Setup(Level.Trial)
    public void setup() {
        DatabaseInitializer.getDataSource();
        DatabaseInitializer.clearData();

        // 初始化 easy-query
        EasyQueryClient easyQueryClient = EasyQueryBootstrapper.defaultBuilderConfiguration()
                .setDefaultDataSource(DatabaseInitializer.getDataSource())
                .useDatabaseConfigure(new H2DatabaseConfiguration())
                .build();
        easyEntityQuery = new DefaultEasyEntityQuery(easyQueryClient);

        // 初始化 JOOQ
        jooqDsl = DSL.using(DatabaseInitializer.getDataSource(), SQLDialect.H2);

        // 初始化 Hibernate
        entityManager = HibernateUtil.createEntityManager();

        // 插入测试数据
        insertTestData();
    }

    private void insertTestData() {
        for (int i = 0; i < 500; i++) {
            String userId = UUID.randomUUID().toString();
            DatabaseInitializer.insertUserWithJdbc(userId, "user_" + i, "user" + i + "@example.com", 20 + (i % 50), "1234567890", "Address " + i);

            int orderCount = 2 + (i % 4);
            for (int j = 0; j < orderCount; j++) {
                String orderId = UUID.randomUUID().toString();
                DatabaseInitializer.insertOrderWithJdbc(
                        orderId,
                        userId,
                        "ORDER_" + i + "_" + j,
                        new BigDecimal((100 + i * 10 + j * 5) + ".50"),
                        j % 3,
                        "Order remark " + i + "_" + j
                );
            }
        }
    }

    @Benchmark
    public List<User> easyQueryJoinQuery() {
        return easyEntityQuery.queryable(User.class)
                .innerJoin(Order.class, (u, o) -> u.id().eq(o.userId()))
                .where((u, o) -> {
                    o.status().eq(1);
                    o.amount().ge(new BigDecimal("100"));
                })
                .distinct()
                .limit(20)
                .toList();
    }

    @Benchmark
    public List<com.easyquery.benchmark.jooq.JooqUser> jooqJoinQuery() {
        return jooqDsl.selectDistinct(
                field("t_user.id").as("id"),
                field("t_user.username").as("username"),
                field("t_user.email").as("email"),
                field("t_user.age").as("age"),
                field("t_user.phone").as("phone"),
                field("t_user.address").as("address")
        )
                .from(table("t_user"))
                .join(table("t_order")).on(field("t_user.id").eq(field("t_order.user_id")))
                .where(field("t_order.status").eq(1)
                        .and(field("t_order.amount").ge(new BigDecimal("100"))))
                .limit(20)
                .fetchInto(com.easyquery.benchmark.jooq.JooqUser.class);
    }

    @Benchmark
    public long easyQueryAggregation() {
        return easyEntityQuery.queryable(Order.class)
                .where(o -> o.status().eq(1))
                .count();
    }

    @Benchmark
    public long jooqAggregation() {
        Integer count = jooqDsl.selectCount()
                .from(table("t_order"))
                .where(field("status").eq(1))
                .fetchOne(0, Integer.class);
        return count != null ? count : 0;
    }

    @Benchmark
    public List<HibernateUser> hibernateJoinQuery() {
        TypedQuery<HibernateUser> query = entityManager.createQuery(
                "SELECT DISTINCT u FROM HibernateUser u " +
                "JOIN HibernateOrder o ON u.id = o.userId " +
                "WHERE o.status = :status AND o.amount >= :minAmount",
                HibernateUser.class);
        query.setParameter("status", 1);
        query.setParameter("minAmount", new BigDecimal("100"));
        query.setMaxResults(20);
        return query.getResultList();
    }

    @Benchmark
    public long hibernateAggregation() {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(o) FROM HibernateOrder o WHERE o.status = :status",
                Long.class);
        query.setParameter("status", 1);
        return query.getSingleResult();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        DatabaseInitializer.clearData();
    }
}
