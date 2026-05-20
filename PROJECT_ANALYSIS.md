# Project Analysis — Swayam_Prakash_Sahoo (Library Loan Management)

## Purpose
This mini-project demonstrates a compact Library Loan Management System built
with Java and Apache Derby (embedded). It is designed for teaching and testing
core JDBC features: schema creation, DML/DDL, transactions with savepoints,
prepared statements, indexing, and basic performance measurement.

## High-level architecture

The application is structured as a small set of focused classes:

- ConnectionManager — Derby embedded driver initialization and connection factory.
- SchemaInitializer — Idempotent schema creation and seed data insertion.
- BusinessLogic — Core use-cases: register member, add book, process loan,
  return book, query active loans. Demonstrates transactions and savepoints.
- PerformanceEvaluator — Benchmark suite to exercise insertion and query
  workloads and append results to perf_report.csv.
- MainApp — Menu-driven CLI for manual use and testing.

## Data model and schema

Tables:

- Members(MemberID, Name, ActiveLoanCount)
- Books(BookID, ISBN, Title, Available)
- Loans(LoanID, BookID, MemberID, LoanDate, ReturnDate)

Primary keys:

- Members.MemberID (identity)
- Books.BookID (identity)
- Loans.LoanID (identity)

Foreign keys:

- Loans.BookID → Books.BookID
- Loans.MemberID → Members.MemberID

Indexes:

- IDX_BOOK_ISBN — speeds up ISBN lookups
- IDX_LOAN_MEMBER — speeds up per-member loan queries
- IDX_LOAN_RETURN — speeds up return queries and active-loan filtering

## Transaction flows (key paths)

### processLoan(bookId, memberId)

1) Validate book existence and availability
2) Reserve the book (Books.Available = 0)
3) Savepoint after book reservation
4) Insert loan row (Loans)
5) Increment member ActiveLoanCount
6) Commit or rollback to savepoint on partial failure

This demonstrates fine-grained rollback and consistency guarantees.

### returnBook(loanId)

1) Validate loan exists and is active
2) Update Loans.ReturnDate
3) Mark book as available
4) Decrement member ActiveLoanCount

## Demo workflow

The scripted demo (demo_mini_project.sh) runs a deterministic sequence:

- Initialize schema
- Register a member
- Add books
- Process loans
- Query active loans by member
- (Optional) Run performance evaluation

This is suitable for screenshots, CI, and grading because the output is stable.

## Performance evaluation

The PerformanceEvaluator runs a few synthetic workloads and appends a report
to perf_report.csv (if enabled in the demo). This helps compare basic query and
insert performance across configurations.

## Extensibility ideas

- Add due dates and overdue fines
- Add a reservation queue for popular books
- Add a REST API or UI (Spark/JavaFX/Spring Boot)
- Swap Derby for PostgreSQL/MySQL by changing ConnectionManager and SQL dialects

## Best practices used

- PreparedStatement for SQL parameterization (mitigates SQL injection)
- Explicit transaction boundaries with rollback and savepoints
- Idempotent schema initialization for repeated runs
- Clear separation of concerns (connection, schema, logic, UI)

---

Author: Swayam Prakash Sahoo
Date: 2026-05-19
