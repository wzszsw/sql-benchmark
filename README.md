# Easy-Query vs JOOQ vs Hibernate Performance Benchmark

## 🏆 Benchmark Results

**Test Environment:**
- **OS**: Windows 10 (Build 26200)
- **JDK**: OpenJDK 21.0.9+10-LTS (64-Bit Server VM)
- **Database**: H2 2.2.224 (In-Memory)
- **Connection Pool**: HikariCP 4.0.3
- **JMH**: 1.37

### Performance Summary (ops/s - higher is better)

| Test Scenario | EasyQuery | Hibernate | JOOQ | Best |
|--------------|-----------|-----------|------|------|
| **Query Operations** |
| Select by ID | 248,707 ± 11,189 | 160,880 ± 8,063 | 119,428 ± 5,480 | 🚀 **1.55x faster** than Hibernate |
| Select List | 200,701 ± 7,415 | 262,477 ± 9,327 | 63,652 ± 2,016 | ❌ **0.76x** (Hibernate wins) |
| COUNT Query | 339,418 ± 14,287 | 358,731 ± 21,243 | 160,317 ± 6,168 | ❌ **0.95x** (Hibernate wins) |
| **Insert Operations** |
| Single Insert | 64,788 ± 1,830 | 205 ± 19 | 49,635 ± 1,540 | 🚀 **1.31x faster** than JOOQ |
| Batch Insert (1000) | 72.02 ± 2.84 | 66.57 ± 2.03 | 43.34 ± 1.23 | 🚀 **1.08x faster** than Hibernate |
| **Update Operations** |
| Update by ID | 114,134 ± 3,289 | 89,663 ± 3,320 | 92,446 ± 2,492 | 🚀 **1.27x faster** than Hibernate |
| Batch Update | 3,638 ± 136 | 3,472 ± 120 | 3,493 ± 123 | 🚀 **1.05x faster** than Hibernate |
| **Delete Operations** |
| Delete by Condition | 287,141 ± 15,493 | 219,065 ± 8,967 | 204,237 ± 7,796 | 🚀 **1.31x faster** than Hibernate |
| **Complex Operations** |
| JOIN Query | 139,597 ± 6,754 | 175,508 ± 8,876 | 32,434 ± 1,978 | ❌ **0.80x** (Hibernate wins) |
| Subquery (IN + GROUP BY + HAVING) | 94,714 ± 4,063 | 183,271 ± 9,453 | 36,589 ± 852 | ❌ **0.52x** (Hibernate wins) |

### Key Findings

✅ **EasyQuery advantages:**
- **Select by ID**: 1.55x faster than Hibernate, 2.08x faster than JOOQ (248,707 vs 160,880 vs 119,428 ops/s)
- **Single insert operations**: 316x faster than Hibernate, 1.31x faster than JOOQ (64,788 vs 205 vs 49,635 ops/s)
- **Batch insert operations**: 1.08x faster than Hibernate, 1.66x faster than JOOQ
- **Update by ID**: 1.27x faster than Hibernate, 1.23x faster than JOOQ
- **Batch update**: 1.05x faster than Hibernate, 1.04x faster than JOOQ
- **Delete by condition**: 1.31x faster than Hibernate, 1.41x faster than JOOQ
- Best all-around performer in **CRUD operations**, especially in insert, update and delete operations

❌ **EasyQuery weaknesses:**
- **Select List**: 0.76x vs Hibernate (but still 3.15x faster than JOOQ)
- **COUNT query**: 0.95x vs Hibernate (but still 2.12x faster than JOOQ)
- **Complex queries**: Hibernate shows significant advantage in JOIN (0.80x) and subquery (0.52x) scenarios

✅ **JOOQ advantages:**
- **Consistent low variance**: Most stable performance across benchmarks
- **Predictable SQL generation**: Direct SQL control

❌ **JOOQ weaknesses:**
- **Overall performance**: Significantly slower than both EasyQuery and Hibernate in most scenarios
- **Complex queries**: Much slower than both frameworks

✅ **Hibernate advantages:**
- **SELECT operations**: Strong in list queries (262,477 ops/s)
- **Complex queries (JOIN, Subquery)**: 1.26-1.93x faster than EasyQuery, dominates complex query scenarios
- **COUNT query**: 1.06x faster than EasyQuery
- **Strong in analytical queries**

❌ **Hibernate weaknesses:**
- **Single insert**: Extremely poor performance (205 ops/s) - 316x slower than EasyQuery
- **Select by ID**: 1.55x slower than EasyQuery
- **Updates and deletes**: 1.05-1.31x slower than EasyQuery

💡 **Overall**: 
- **EasyQuery**: 🏆 **Best for CRUD-intensive applications** - Wins in 6 out of 10 benchmarks, with exceptional performance in basic CRUD operations: single insert (316x faster than Hibernate, 1.31x faster than JOOQ), updates (1.05-1.27x faster), and deletes (1.31x faster). Strong in most query scenarios except complex analytical queries.
- **Hibernate**: 🎯 **Best for complex analytical queries** - Dominates in complex operations like JOIN (1.26x) and subquery (1.93x), excellent for reporting and analytical workloads, but very poor single insert performance.
- **JOOQ**: ⚖️ **Stable but slower** - Consistent performance but generally slower across the board, suitable when direct SQL control and predictability are priorities.

### ⚠️ Important Notes

- All frameworks run in **autocommit mode** without explicit transaction management for fair comparison
- **Test data varies by benchmark**:
  - Query operations: 1,000 users pre-loaded
  - Complex queries: 500 users + ~1,750 orders pre-loaded
  - Update operations: 100 users per iteration
  - Delete operations: 50 users per iteration
  - Insert operations: starts from empty database
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

## 📊 Overview

This is a **standalone** comprehensive performance benchmark comparing **easy-query**, **JOOQ**, and **Hibernate** using JMH (Java Microbenchmark Harness).

> **Talk is cheap, show me the code and benchmarks!**

This project provides objective benchmark data to prove that easy-query is not just a JOOQ clone, but offers significant advantages in both performance and usability compared to other popular ORM frameworks.

### ✨ Standalone Project

This benchmark can be run **independently** without the easy-query parent project:
- ✅ No parent POM dependency
- ✅ Self-contained configuration
- ✅ Clone and run anywhere
- ✅ All dependencies explicitly declared

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

### Example 1: Select by ID (⚡ EasyQuery: 249K ops/s - 1.55x faster than Hibernate)

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

### Example 2: Select List with Filter and Sort (⚡ EasyQuery: 201K ops/s)

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

### Example 3: COUNT Query (⚡ EasyQuery: 339K ops/s)

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

### Example 4: JOIN Query (⚡ EasyQuery: 140K ops/s)

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

### Example 5: Single Insert (⚡ EasyQuery: 64.8K ops/s - 316x faster than Hibernate!)

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

### Example 6: Batch Insert (⚡ EasyQuery: 72.0 ops/s - 1.08x faster)

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

### Example 7: Update by ID (⚡ EasyQuery: 114K ops/s - 1.27x faster)

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

### Example 8: Delete by Condition (⚡ EasyQuery: 287K ops/s - 1.31x faster)

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

### Example 9: Subquery with GROUP BY and HAVING (⚡ Hibernate: 183K ops/s - 1.93x faster)

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

## 💡 Key Advantages

### easy-query Advantages:

1. **🚀 Outstanding Select by ID Performance**: 1.55x faster than Hibernate, 2.08x faster than JOOQ (248,707 vs 160,880 vs 119,428 ops/s)
2. **🏆 Excellent Insert Performance**: 316x faster than Hibernate in single inserts (64,788 vs 205 ops/s), 1.31x faster than JOOQ
3. **💪 Superior Update Performance**: 1.05-1.27x faster than both Hibernate and JOOQ
4. **✨ Type Safety**: Proxy-based strongly-typed API with compile-time checking
5. **🎯 Usability**: Lambda expression style, more in line with Java development habits
6. **📦 Auto-Mapping**: Automatic object mapping (JOOQ also supports `fetchInto()` for basic mapping)
7. **🔧 Flexibility**: Supports multiple query methods and databases
8. **📝 Cleaner Code**: Less boilerplate code, especially for complex queries
9. **🔥 Strong Delete Operations**: 1.31x faster than Hibernate in conditional deletes

### Comparison with Other Frameworks:

**vs Hibernate:**
- **✅ Wins**: Select by ID (1.55x), single inserts (316x), batch inserts (1.08x), updates (1.05-1.27x), deletes (1.31x)
- **❌ Loses**: Select list (0.76x), COUNT queries (0.95x), JOIN queries (0.80x), subquery (0.52x)
- **Type Safety**: Better compile-time type checking with lambda expressions
- **Simplicity**: No need for EntityManager transaction management in simple queries
- **Best Use Case**: CRUD-intensive applications with frequent inserts, updates, and deletes

**vs JOOQ:**
- **✅ Wins**: Select by ID (2.08x), select list (3.15x), batch inserts (1.66x), updates (1.04-1.23x), deletes (1.41x), COUNT queries (2.12x), JOIN queries (4.30x), subquery (2.59x)
- **❌ Loses**: Single inserts (0.77x) - but EasyQuery is still very fast at 64,788 ops/s
- **SQL Control**: Both provide direct control over generated SQL
- **Learning Curve**: Similar learning curve for developers familiar with SQL
- **Best Use Case**: Better all-around choice than JOOQ for most scenarios

## 🤝 Contributing

Issues and Pull Requests are welcome!

If you find any problems with the tests or have suggestions for improvements, please feel free to open an issue.

## 📄 License

This project follows the Apache License 2.0.

## 🔗 Related Links

- [easy-query GitHub](https://github.com/xuejmnet/easy-query)
- [easy-query Documentation](https://xuejmnet.github.io/easy-query-doc/)
- [JOOQ Official Site](https://www.jooq.org/)
- [JMH Official Site](https://github.com/openjdk/jmh)
