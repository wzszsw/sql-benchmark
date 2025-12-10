package com.easyquery.benchmark;

import com.easy.query.core.proxy.sql.GroupKeys;
import com.easyquery.benchmark.entity.Order;
import com.easyquery.benchmark.entity.User;
import com.easyquery.benchmark.jooq.generated.tables.pojos.TUser;
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

import static com.easyquery.benchmark.jooq.generated.Tables.T_ORDER;
import static com.easyquery.benchmark.jooq.generated.Tables.T_USER;


@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Benchmark)
@Warmup(iterations = 10, time = 5)
@Measurement(iterations = 15, time = 5)
@Fork(value = 3, jvmArgs = {"-Xms2g", "-Xmx2g", "-XX:+UseG1GC"})
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

        EasyQueryClient easyQueryClient = EasyQueryBootstrapper.defaultBuilderConfiguration()
                .setDefaultDataSource(DatabaseInitializer.getDataSource())
                .optionConfigure(op->{
                    op.setPrintSql(false);
                })
                .useDatabaseConfigure(new H2DatabaseConfiguration())
                .build();
        easyEntityQuery = new DefaultEasyEntityQuery(easyQueryClient);

        jooqDsl = DSL.using(DatabaseInitializer.getDataSource(), SQLDialect.H2);

        entityManager = HibernateUtil.createEntityManager();

        insertTestData();
    }

    @Setup(Level.Iteration)
    public void setupIteration() {
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
    public List<TUser> jooqJoinQuery() {
        return jooqDsl.selectDistinct(T_USER.fields())
                .from(T_USER)
                .join(T_ORDER).on(T_USER.ID.eq(T_ORDER.USER_ID))
                .where(T_ORDER.STATUS.eq(1)
                        .and(T_ORDER.AMOUNT.ge(new BigDecimal("100"))))
                .limit(20)
                .fetchInto(TUser.class);
    }

    @Benchmark
    public List<User> easyQuerySubquery() {
        // Query users whose total order amount exceeds 500
        return easyEntityQuery.queryable(User.class)
                .where(u -> {
                    u.id().in(
                        easyEntityQuery.queryable(Order.class)
                            .where(o -> o.status().eq(1))
                            .groupBy(o -> GroupKeys.of(o.userId()))
                            .having(o -> o.groupTable().amount().sum().gt(new BigDecimal("500")))
                            .select(o -> o.groupTable().userId())
                    );
                })
                .limit(20)
                .toList();
    }

    @Benchmark
    public List<TUser> jooqSubquery() {
        // Query users whose total order amount exceeds 500
        return jooqDsl.select(T_USER.fields())
                .from(T_USER)
                .where(T_USER.ID.in(
                    jooqDsl.select(T_ORDER.USER_ID)
                        .from(T_ORDER)
                        .where(T_ORDER.STATUS.eq(1))
                        .groupBy(T_ORDER.USER_ID)
                        .having(DSL.sum(T_ORDER.AMOUNT).gt(new BigDecimal("500")))
                ))
                .limit(20)
                .fetchInto(TUser.class);
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
    public List<HibernateUser> hibernateSubquery() {
        // Query users whose total order amount exceeds 500
        TypedQuery<HibernateUser> query = entityManager.createQuery(
                "SELECT u FROM HibernateUser u " +
                "WHERE u.id IN (" +
                "  SELECT o.userId FROM HibernateOrder o " +
                "  WHERE o.status = :status " +
                "  GROUP BY o.userId " +
                "  HAVING SUM(o.amount) > :minTotalAmount" +
                ")",
                HibernateUser.class);
        query.setParameter("status", 1);
        query.setParameter("minTotalAmount", new BigDecimal("500"));
        query.setMaxResults(20);
        return query.getResultList();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
        DatabaseInitializer.clearData();
    }
}
