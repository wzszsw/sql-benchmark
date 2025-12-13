# Easy-Query vs JOOQ vs Hibernate Performance Benchmark

## 🏆 Benchmark Results

**Test Environment:**
- **OS**: Windows 10 (Build 26200)
- **JDK**: OpenJDK 21.0.9+10-LTS (64-Bit Server VM)
- **Database**: H2 2.2.224 (In-Memory)
- **Connection Pool**: HikariCP 4.0.3
- **JMH**: 1.37

### Performance Summary (ops/s - higher is better)

#### 📊 Query Operations

**Select by ID** - 🏆 EasyQuery wins (1.13x faster than Hibernate)
```
EasyQuery    █████████████████████████████████████████████████ 298,303 ± 5,239
Hibernate    ████████████████████████████████████████████      264,571 ± 4,928
JOOQ         ████████████████████                              132,786 ± 3,480
```

**Select List** - 🏆 EasyQuery wins (1.75x faster than Hibernate)
```
EasyQuery    █████████████████████████████████████████████████ 247,088 ± 6,488
Hibernate    ████████████████████████████                      141,050 ± 4,415
JOOQ         █████████████                                      68,773 ± 2,041
```

**COUNT Query** - 🏆 Hibernate wins (1.01x faster than EasyQuery)
```
EasyQuery    ████████████████████████████████████████████████  382,545 ± 8,542
Hibernate    █████████████████████████████████████████████████ 385,362 ± 7,257
JOOQ         █████████████████████████                         197,704 ± 5,374
```

#### 📊 Insert Operations

**Single Insert** - 🏆 EasyQuery wins (1.11x faster than Hibernate)
```
EasyQuery    █████████████████████████████████████████████████  63,866 ± 2,028
Hibernate    ███████████████████████████████████████████████    57,385 ± 1,241
JOOQ         ███████████████████████████████████████            50,257 ± 1,845
```

**Batch Insert (1000 records)** - 🏆 EasyQuery wins (1.02x faster than Hibernate)
```
EasyQuery    █████████████████████████████████████████████████  72.06 ± 4.65
Hibernate    ████████████████████████████████████████████████   70.66 ± 1.02
JOOQ         ██████████████████████████████                     43.54 ± 2.25
```

#### 📊 Update Operations

**Update by ID** - 🏆 EasyQuery wins (1.36x faster than Hibernate)
```
EasyQuery    █████████████████████████████████████████████████ 125,902 ± 3,013
Hibernate    ████████████████████████████████████               92,470 ± 1,730
JOOQ         ███████████████████████████████████████           100,361 ± 1,750
```

**Batch Update** - 🏆 EasyQuery wins (1.08x faster than Hibernate)
```
EasyQuery    █████████████████████████████████████████████████  3,959 ± 69
Hibernate    ██████████████████████████████████████████████     3,672 ± 73
JOOQ         █████████████████████████████████████████████████  3,909 ± 63
```

#### 📊 Delete Operations

**Delete by Condition** - 🏆 JOOQ wins (1.11x faster than EasyQuery)
```
EasyQuery    █████████████████████████████████████████████      52,406 ± 2,669
Hibernate    ████████████████████████                           28,902 ± 5,694
JOOQ         █████████████████████████████████████████████████  58,169 ± 28,273
```

#### 📊 Complex Operations

**JOIN Query** - 🏆 EasyQuery wins (2.46x faster than Hibernate, 23.72x faster than JOOQ!)
```
EasyQuery    █████████████████████████████████████████████████ 138,963 ± 8,584
Hibernate    ████████████████████                               56,437 ± 14,662
JOOQ         █                                                   5,859 ± 195
```

**Subquery (IN + GROUP BY + HAVING)** - 🏆 EasyQuery wins (6.46x faster than Hibernate, 19.01x faster than JOOQ!)
```
EasyQuery    █████████████████████████████████████████████████ 100,725 ± 3,692
Hibernate    ███████                                            15,594 ± 593
JOOQ         ██                                                  5,296 ± 180
```

### ⚠️ Important Notes

- **Transaction handling**: Insert/Update/Delete operations use **explicit transaction management** for data integrity; Query operations run without transactions
- **Test data varies by benchmark**:
  - Query operations: 1,000 users pre-loaded (at Trial level)
  - Complex queries: 500 users + ~1,750 orders pre-loaded (at Trial level)
  - Update operations: 100 users pre-loaded (at Trial level)
  - Delete operations: 50 users per iteration (refreshed each iteration)
  - Insert operations: starts from empty database (cleared each iteration)
- Connection pool: HikariCP with 10 max connections, 5 min idle
- Benchmark stability achieved through:
  - **Warmup**: 10 iterations × 5 seconds
  - **Measurement**: 15 iterations × 5 seconds
  - **Forks**: 3 JVM forks for statistical reliability

### 📢 Disclaimer

**About Test Fairness**: Due to limited time and resources, the author may not have deep expertise in all the optimization mechanisms of each ORM framework. If you believe any benchmark is unfair or not optimized properly, you are **welcome and encouraged** to:

- 🔧 Fork this repository
- 📝 Modify the benchmark code with your optimizations
- 🚀 Re-run the tests and share your results

**Author's Confidence**: Despite these limitations, we are confident that **EasyQuery delivers excellent performance** in real-world scenarios. The benchmarks demonstrate its strengths, but we remain open to improvements and community feedback.

💡 **Contributions are welcome!** If you find better ways to optimize any framework's performance, please submit a pull request. Fair comparison benefits everyone in the community.

---

## 🎯 Test Scenarios

All benchmarks use JMH (Java Microbenchmark Harness) with the following configuration:
- **Mode**: Throughput (operations per second)
- **Warmup**: 10 iterations, 5 seconds each
- **Measurement**: 15 iterations, 5 seconds each
- **Fork**: 3 JVM forks
- **Threads**: 1 thread

### 1. **Insert Operations (InsertBenchmark)**
- **Single insert**: Insert one user record at a time
- **Batch insert (1000 records)**: Insert 1000 user records in a single batch operation

### 2. **Query Operations (QueryBenchmark)**
- **Query by ID**: Select a single user by primary key
- **Conditional query**: Select users with age >= 25, sorted by username DESC, limit 10
- **COUNT aggregation**: Count users with age between 25 and 35

### 3. **Update Operations (UpdateBenchmark)**
- **Single record update by ID**: Update one user's age by ID
- **Batch conditional update**: Update multiple users' age where age >= 50

### 4. **Delete Operations (DeleteBenchmark)**
- **Conditional delete**: Delete users where age >= 40

### 5. **Complex Queries (ComplexQueryBenchmark)**
- **JOIN query**: INNER JOIN users and orders with filtering (status=1, amount>=100), distinct results, limit 20
- **Subquery with aggregation**: Query users whose total order amount exceeds 500, using IN subquery with GROUP BY and HAVING

## 🔧 Tech Stack

- **JMH**: 1.37 - Java Microbenchmark Harness
- **H2 Database**: 2.2.224 - In-memory database
- **easy-query**: 3.1.66-preview3
- **JOOQ**: 3.19.1
- **Hibernate**: 6.4.1.Final
- **HikariCP**: 4.0.3 - Connection pool

## 🚀 Running the Benchmarks

### Prerequisites

- JDK 21 or higher
- Maven 3.6+

### Quick Start (Recommended)

Use the provided scripts for an automated build and test process:

**Windows:**
```bash
run-benchmark.bat
```

**Linux/macOS:**
```bash
chmod +x run-benchmark.sh
./run-benchmark.sh
```

These scripts will:
1. Build the project with Maven
2. Run all benchmarks
3. Save results to `results/benchmark-results.json`

### Manual Build and Run

### Build the Project

```bash
cd sql-benchmark
mvn clean package
```

### Run All Benchmarks

**Windows:**
```bash
run-benchmark.bat
```

**Linux/macOS:**
```bash
./run-benchmark.sh
```

Or manually:
```bash
java -jar target/benchmarks.jar
```

### Run Specific Benchmarks

```bash
# Insert tests only
java -jar target/benchmarks.jar InsertBenchmark

# Query tests only
java -jar target/benchmarks.jar QueryBenchmark

# Update tests only
java -jar target/benchmarks.jar UpdateBenchmark

# Delete tests only
java -jar target/benchmarks.jar DeleteBenchmark

# Complex query tests only
java -jar target/benchmarks.jar ComplexQueryBenchmark
```

### Custom Test Parameters

```bash
# Increase warmup and test iterations
java -jar target/benchmarks.jar -wi 5 -i 10

# Multi-threaded testing
java -jar target/benchmarks.jar -t 4

# Verbose output
java -jar target/benchmarks.jar -v EXTRA

# Output to JSON file
java -jar target/benchmarks.jar -rf json -rff results/benchmark-results.json
```

## 📊 Visualizing Results

After running the tests, results are saved in the `results/` directory. You can visualize them using JMH Visualizer:

1. Visit: http://jmh.morethan.io/
2. Upload `results/benchmark-results.json`
3. View interactive performance comparison charts

## 🔍 Code Comparison

### Example 1: Select by ID (⚡ EasyQuery: 298K ops/s - 1.13x faster than Hibernate)

**EasyQuery** - Type-safe with lambda:
```java
User user = easyEntityQuery.queryable(User.class)
    .where(u -> u.id().eq(userId))
    .firstOrNull();
```

**JOOQ** - Type-safe with generated tables:
```java
TUser user = jooqDsl.selectFrom(T_USER)
    .where(T_USER.ID.eq(userId))
    .fetchOneInto(TUser.class);
```

**Hibernate** - String-based native query:
```java
HibernateUser user = (HibernateUser) entityManager.createNativeQuery(
    "SELECT * FROM t_user WHERE id = ?", HibernateUser.class)
    .setParameter(1, userId)
    .getSingleResult();
```

### Example 2: Select List with Filter and Sort (⚡ EasyQuery: 247K ops/s - 1.75x faster than Hibernate)

**EasyQuery** - Fluent API with lambda expressions:
```java
List<User> users = easyEntityQuery.queryable(User.class)
    .where(u -> u.age().ge(25))
    .orderBy(u -> u.username().desc())
    .limit(10)
    .toList();
```

**JOOQ** - SQL-style with generated constants:
```java
List<TUser> users = jooqDsl.selectFrom(T_USER)
    .where(T_USER.AGE.ge(25))
    .orderBy(T_USER.USERNAME.desc())
    .limit(10)
    .fetchInto(TUser.class);
```

**Hibernate** - HQL with string parameters:
```java
TypedQuery<HibernateUser> query = entityManager.createQuery(
    "SELECT u FROM HibernateUser u WHERE u.age >= :age ORDER BY u.username DESC",
    HibernateUser.class);
query.setParameter("age", 25);
query.setMaxResults(10);
List<HibernateUser> users = query.getResultList();
```

### Example 3: COUNT Query (⚡ EasyQuery: 383K ops/s - tied with Hibernate)

**EasyQuery** - Clean condition block:
```java
long count = easyEntityQuery.queryable(User.class)
    .where(u -> {
        u.age().ge(25);
        u.age().le(35);
    })
    .count();
```

**JOOQ** - Chained conditions with null check:
```java
Integer count = jooqDsl.selectCount()
    .from(T_USER)
    .where(T_USER.AGE.ge(25).and(T_USER.AGE.le(35)))
    .fetchOne(0, Integer.class);
```

**Hibernate** - Named parameters:
```java
TypedQuery<Long> query = entityManager.createQuery(
    "SELECT COUNT(u) FROM HibernateUser u WHERE u.age >= :minAge AND u.age <= :maxAge",
    Long.class);
query.setParameter("minAge", 25);
query.setParameter("maxAge", 35);
long count = query.getSingleResult();
```

### Example 4: JOIN Query (⚡ EasyQuery: 139K ops/s - 2.46x faster than Hibernate)

**EasyQuery** - Type-safe JOIN with tuple syntax:
```java
List<User> users = easyEntityQuery.queryable(User.class)
    .innerJoin(Order.class, (u, o) -> u.id().eq(o.userId()))
    .where((u, o) -> {
        o.status().eq(1);
        o.amount().ge(new BigDecimal("100"));
    })
    .distinct()
    .limit(20)
    .toList();
```

**JOOQ** - SQL-style with explicit field selection:
```java
List<TUser> users = jooqDsl.selectDistinct(T_USER.fields())
    .from(T_USER)
    .join(T_ORDER).on(T_USER.ID.eq(T_ORDER.USER_ID))
    .where(T_ORDER.STATUS.eq(1)
        .and(T_ORDER.AMOUNT.ge(new BigDecimal("100"))))
    .limit(20)
    .fetchInto(TUser.class);
```

**Hibernate** - HQL with multiple parameters:
```java
TypedQuery<HibernateUser> query = entityManager.createQuery(
    "SELECT DISTINCT u FROM HibernateUser u " +
    "JOIN HibernateOrder o ON u.id = o.userId " +
    "WHERE o.status = :status AND o.amount >= :minAmount",
    HibernateUser.class);
query.setParameter("status", 1);
query.setParameter("minAmount", new BigDecimal("100"));
query.setMaxResults(20);
List<HibernateUser> users = query.getResultList();
```

### Example 5: Single Insert (⚡ EasyQuery: 63.9K ops/s - 1.11x faster than Hibernate)

**EasyQuery** - Object-based with transaction:
```java
try (Transaction transaction = easyEntityQuery.beginTransaction()) {
    User user = new User(id, "username", "email@example.com", 25, "1234567890", "Address");
    easyEntityQuery.insertable(user).executeRows();
    transaction.commit();
}
```

**JOOQ** - SQL-style builder with transaction:
```java
jooqDsl.transaction(configuration -> {
    DSL.using(configuration)
        .insertInto(T_USER)
        .set(T_USER.ID, id)
        .set(T_USER.USERNAME, "username")
        .set(T_USER.EMAIL, "email@example.com")
        .set(T_USER.AGE, 25)
        .set(T_USER.PHONE, "1234567890")
        .set(T_USER.ADDRESS, "Address")
        .execute();
});
```

**Hibernate** - Entity persist with manual transaction:
```java
entityManager.getTransaction().begin();
try {
    HibernateUser user = new HibernateUser(id, "username", "email@example.com", 
                                           25, "1234567890", "Address");
    entityManager.persist(user);
    entityManager.getTransaction().commit();
} catch (Exception e) {
    if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
    }
    throw e;
}
```

### Example 6: Batch Insert (⚡ EasyQuery: 72.1 ops/s - 1.02x faster than Hibernate)

**EasyQuery** - Simple batch method:
```java
try (Transaction transaction = easyEntityQuery.beginTransaction()) {
    List<User> users = new ArrayList<>();
    for (int i = 0; i < 1000; i++) {
        users.add(new User(UUID.randomUUID().toString(), "user_" + i, 
                          "user@example.com", 25 + i, "1234567890", "Address"));
    }
    easyEntityQuery.insertable(users).batch(true).executeRows();
    transaction.commit();
}
```

**JOOQ** - Batch with records:
```java
List<TUserRecord> records = new ArrayList<>();
for (int i = 0; i < 1000; i++) {
    TUserRecord record = new TUserRecord();
    record.setId(UUID.randomUUID().toString());
    record.setUsername("user_" + i);
    // ... set other fields
    records.add(record);
}
jooqDsl.transaction(configuration -> {
    DSL.using(configuration).batchInsert(records).execute();
});
```

**Hibernate** - Loop with flush/clear:
```java
entityManager.getTransaction().begin();
try {
    for (int i = 0; i < 1000; i++) {
        HibernateUser user = new HibernateUser(UUID.randomUUID().toString(), 
                                               "user_" + i, "user@example.com", 
                                               25 + i, "1234567890", "Address");
        entityManager.persist(user);
    }
    entityManager.flush();
    entityManager.clear();
    entityManager.getTransaction().commit();
} catch (Exception e) {
    if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
    }
    throw e;
}
```

### Example 7: Update by ID (⚡ EasyQuery: 126K ops/s - 1.36x faster than Hibernate)

**EasyQuery** - Fluent update API:
```java
try (Transaction transaction = easyEntityQuery.beginTransaction()) {
    long updated = easyEntityQuery.updatable(User.class)
        .setColumns(u -> u.age().set(99))
        .where(u -> u.id().eq(userId))
        .executeRows();
    transaction.commit();
}
```

**JOOQ** - SQL-style update:
```java
int updated = jooqDsl.transactionResult(configuration -> {
    return DSL.using(configuration)
        .update(T_USER)
        .set(T_USER.AGE, 99)
        .where(T_USER.ID.eq(userId))
        .execute();
});
```

**Hibernate** - HQL update with manual transaction:
```java
entityManager.getTransaction().begin();
try {
    Query query = entityManager.createQuery(
        "UPDATE HibernateUser u SET u.age = :age WHERE u.id = :id");
    query.setParameter("age", 99);
    query.setParameter("id", userId);
    int updated = query.executeUpdate();
    entityManager.getTransaction().commit();
} catch (Exception e) {
    if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
    }
    throw e;
}
```

### Example 8: Delete by Condition (⚡ EasyQuery: 52.4K ops/s - 1.81x faster than Hibernate)

**EasyQuery** - Lambda-based delete:
```java
try (Transaction transaction = easyEntityQuery.beginTransaction()) {
    long deleted = easyEntityQuery.deletable(User.class)
        .allowDeleteStatement(true)
        .where(u -> u.age().ge(40))
        .executeRows();
    transaction.commit();
}
```

**JOOQ** - SQL-style delete:
```java
int deleted = jooqDsl.transactionResult(configuration -> {
    return DSL.using(configuration)
        .deleteFrom(T_USER)
        .where(T_USER.AGE.ge(40))
        .execute();
});
```

**Hibernate** - HQL delete with manual transaction:
```java
entityManager.getTransaction().begin();
try {
    Query query = entityManager.createQuery(
        "DELETE FROM HibernateUser u WHERE u.age >= :minAge");
    query.setParameter("minAge", 40);
    int deleted = query.executeUpdate();
    entityManager.getTransaction().commit();
} catch (Exception e) {
    if (entityManager.getTransaction().isActive()) {
        entityManager.getTransaction().rollback();
    }
    throw e;
}
```

### Example 9: Subquery with GROUP BY and HAVING (⚡ EasyQuery: 100.7K ops/s - 6.46x faster than Hibernate)

**EasyQuery** - Type-safe subquery with lambda:
```java
// Query users whose total order amount exceeds 500
List<User> users = easyEntityQuery.queryable(User.class)
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
```

**JOOQ** - SQL-style subquery:
```java
// Query users whose total order amount exceeds 500
List<TUser> users = jooqDsl.select(T_USER.fields())
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
```

**Hibernate** - JPQL subquery:
```java
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
List<HibernateUser> users = query.getResultList();
```

## 📄 License

This project follows the Apache License 2.0.

## 🔗 Related Links

- [easy-query GitHub](https://github.com/xuejmnet/easy-query)
- [easy-query Documentation](https://xuejmnet.github.io/easy-query-doc/en)
- [JOOQ Official Site](https://www.jooq.org/)
- [JMH Official Site](https://github.com/openjdk/jmh)
