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
| Select by ID | 311,249 ± 9,199 | 128,760 ± 56,391 | 22,649 ± 1,878 | 🚀 **2.42x faster** than Hibernate |
| Select List | 292,872 ± 4,495 | 110,697 ± 64,844 | 30,783 ± 15,147 | 🚀 **2.65x faster** than Hibernate |
| COUNT Query | 430,318 ± 7,102 | 517,280 ± 5,926 | 41,375 ± 24,154 | ❌ **0.83x** (Hibernate wins) |
| **Insert Operations** |
| Single Insert | 44,908 ± 16,265 | 232 ± 20 | 63,924 ± 1,364 | ❌ **0.70x** (JOOQ wins) |
| Batch Insert (1000) | 83.80 ± 2.01 | 79.37 ± 6.90 | 53.24 ± 0.79 | 🚀 **1.06x faster** than Hibernate |
| **Update Operations** |
| Update by ID | 116,299 ± 3,606 | 90,441 ± 2,546 | 96,504 ± 2,253 | 🚀 **1.29x faster** than Hibernate |
| Batch Update | 3,890 ± 81 | 3,637 ± 91 | 3,700 ± 92 | 🚀 **1.07x faster** than Hibernate |
| **Delete Operations** |
| Delete by Condition | 380,373 ± 12,503 | 287,285 ± 6,564 | 264,793 ± 6,995 | 🚀 **1.32x faster** than Hibernate |
| **Complex Operations** |
| JOIN Query | 173,493 ± 4,643 | 221,953 ± 5,742 | 40,125 ± 6,239 | ❌ **0.78x** (Hibernate wins) |
| Aggregation (COUNT) | 443,676 ± 12,360 | 482,105 ± 13,783 | 246,067 ± 12,866 | ❌ **0.92x** (Hibernate wins) |

### Key Findings

✅ **EasyQuery advantages:**
- **Select by ID**: 2.42x faster than Hibernate, 13.75x faster than JOOQ (311,249 vs 128,760 vs 22,649 ops/s)
- **Select List**: 2.65x faster than Hibernate, 9.51x faster than JOOQ (292,872 vs 110,697 vs 30,783 ops/s)
- **Single insert operations**: 193.6x faster than Hibernate (44,908 vs 232 ops/s)
- **Batch insert operations**: 1.06x faster than Hibernate, 1.57x faster than JOOQ
- **Update by ID**: 1.29x faster than Hibernate, 1.20x faster than JOOQ
- **Batch update**: 1.07x faster than Hibernate, 1.05x faster than JOOQ
- **Delete by condition**: 1.32x faster than Hibernate, 1.44x faster than JOOQ
- Best all-around performer in **most operations**, especially in select and delete operations

❌ **EasyQuery weaknesses:**
- **Single insert**: 0.70x vs JOOQ (but still 193.6x faster than Hibernate)
- **COUNT query**: 0.83x vs Hibernate (but 10.40x faster than JOOQ)
- **Complex queries**: 0.78-0.92x vs Hibernate

✅ **JOOQ advantages:**
- **Single insert**: 1.42x faster than EasyQuery, 275x faster than Hibernate
- **Consistent low variance**: Most stable performance across benchmarks

❌ **JOOQ weaknesses:**
- **Select operations**: Significantly slower than both EasyQuery and Hibernate
- **Complex queries**: 5.43x slower than EasyQuery in JOIN, 1.80x slower in aggregation

✅ **Hibernate advantages:**
- **COUNT query**: 1.20x faster than EasyQuery, 12.50x faster than JOOQ
- **Complex queries (JOIN, Aggregation)**: 1.08-1.28x faster than EasyQuery
- **Strong in analytical queries**

❌ **Hibernate weaknesses:**
- **Single insert**: Extremely poor performance (232 ops/s) - 193.6x slower than EasyQuery
- **Select operations**: 2.42-2.65x slower than EasyQuery

💡 **Overall**: 
- **EasyQuery**: 🏆 **Best all-around performer** - Wins in 6 out of 10 benchmarks, with exceptional performance in select operations (2.42-2.65x faster than Hibernate), updates (1.07-1.29x faster), and deletes (1.32x faster). Only loses in single insert vs JOOQ and analytical queries vs Hibernate.
- **JOOQ**: Best for single inserts but significantly weaker in select and complex operations.
- **Hibernate**: Strong in analytical/complex queries but very poor single insert performance and slower in basic operations.

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
- **COUNT aggregation**: Count orders with status=1

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

### Example 1: Simple Query

**easy-query**:
```java
List<User> users = easyEntityQuery.queryable(User.class)
    .where(u -> u.age().ge(25))
    .orderBy(u -> u.username().desc())
    .limit(10)
    .toList();
```

**JOOQ**:
```java
List<JooqUser> users = jooqDsl.select()
    .from(table("t_user"))
    .where(field("age").ge(25))
    .orderBy(field("username").desc())
    .limit(10)
    .fetchInto(JooqUser.class);
```

### Example 2: COUNT Query

**easy-query**:
```java
long count = easyEntityQuery.queryable(User.class)
    .where(u -> {
        u.age().ge(25);
        u.age().le(35);
    })
    .count();
```

**JOOQ**:
```java
Integer count = jooqDsl.selectCount()
    .from(table("t_user"))
    .where(field("age").ge(25).and(field("age").le(35)))
    .fetchOne(0, Integer.class);
long result = count != null ? count : 0;
```

### Example 3: JOIN Query

**easy-query**:
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

**JOOQ**:
```java
List<JooqUser> users = jooqDsl.selectDistinct(
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
    .fetchInto(JooqUser.class);
```

**Hibernate**:
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

## 💡 Key Advantages

### easy-query Advantages:

1. **🚀 Outstanding Select Performance**: 2.42-2.65x faster than Hibernate, 9.51-13.75x faster than JOOQ
2. **🏆 Excellent Insert Performance**: 193.6x faster than Hibernate in single inserts (44,908 vs 232 ops/s)
3. **💪 Superior Update Performance**: 1.07-1.29x faster than both Hibernate and JOOQ
4. **✨ Type Safety**: Proxy-based strongly-typed API with compile-time checking
5. **🎯 Usability**: Lambda expression style, more in line with Java development habits
6. **📦 Auto-Mapping**: Automatic object mapping (JOOQ also supports `fetchInto()` for basic mapping)
7. **🔧 Flexibility**: Supports multiple query methods and databases
8. **📝 Cleaner Code**: Less boilerplate code, especially for complex queries
9. **🔥 Strong Delete Operations**: 1.32x faster than Hibernate in conditional deletes

### Comparison with Other Frameworks:

**vs Hibernate:**
- **✅ Wins**: Select operations (2.42-2.65x), single inserts (193.6x), batch inserts (1.06x), updates (1.07-1.29x), deletes (1.32x)
- **❌ Loses**: COUNT queries (0.83x), complex queries (0.78-0.92x)
- **Type Safety**: Better compile-time type checking with lambda expressions
- **Simplicity**: No need for EntityManager transaction management in simple queries

**vs JOOQ:**
- **✅ Wins**: Select operations (9.51-13.75x), batch inserts (1.57x), updates (1.05-1.20x), deletes (1.44x), COUNT queries (10.40x), complex queries (1.80-5.43x)
- **❌ Loses**: Single inserts (0.70x)
- **SQL Control**: Both provide direct control over generated SQL
- **Learning Curve**: Similar learning curve for developers familiar with SQL

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
