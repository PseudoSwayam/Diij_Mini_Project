# Swayam_Prakash_Sahoo — Mini JDBC Library (Derby)

Project: swayam_prakash_sahoo
Package identifier: 2341019067 (Java package directory is pkg2341019067)

## Overview

This repository contains a small, self-contained Java console application that
implements a Library Loan Management System using Apache Derby (embedded). The
application demonstrates JDBC programming patterns: connection management,
prepared statements, transactions with savepoints, foreign keys, indexing, and
basic performance measurement.

## Features

- Library member registration
- Book catalog management
- Loan processing with transactional safety and savepoints
- Loan return processing
- Query active loans by member
- Performance evaluation runner

## Repository contents

- src/pkg2341019067/ — Java source files (core application)
- demo_mini_project.sh — scripted demo (non-interactive) that exercises the app
- run.sh — convenience script to compile and launch the interactive CLI
- README.md — this file
- PROJECT_ANALYSIS.md — detailed architecture and design analysis

## Prerequisites

- Java (OpenJDK) installed. Example used in this workspace:
  /opt/homebrew/opt/openjdk/bin/java
- Apache Derby JAR available. Example Homebrew Derby path used here:
  /opt/homebrew/Cellar/derby/10.17.1.0/libexec/lib/derby.jar

## Build & run (interactive)

Compile the sources into out/ and run the interactive CLI:

```bash
javac -cp /opt/homebrew/Cellar/derby/10.17.1.0/libexec/lib/derby.jar -d out -sourcepath src src/pkg2341019067/*.java

java -cp /opt/homebrew/Cellar/derby/10.17.1.0/libexec/lib/derby.jar:out pkg2341019067.MainApp
```

Or use the convenience script (compiles then runs):

```bash
chmod +x run.sh
export DERBY_JAR=/opt/homebrew/Cellar/derby/10.17.1.0/libexec/lib/derby.jar
./run.sh
```

## Demo (non-interactive)

For reproducible output (useful for screenshots and automated tests) run the scripted demo:

```bash
/bin/bash demo_mini_project.sh
```

## Data model (summary)

- Members(MemberID, Name, ActiveLoanCount)
- Books(BookID, ISBN, Title, Available)
- Loans(LoanID, BookID, MemberID, LoanDate, ReturnDate)

Indexes:
- IDX_BOOK_ISBN
- IDX_LOAN_MEMBER
- IDX_LOAN_RETURN

## Transaction design (summary)

Loan processing uses a multi-step transaction with a savepoint to ensure:

1) Book availability is validated
2) Book is reserved (Available = 0)
3) Loan is inserted
4) Member loan count is updated

If a step fails, the transaction is rolled back safely to preserve consistency.

## Notes on package name

Java package names cannot begin with digits. To preserve the requested identifier
2341019067 while remaining valid the project uses pkg2341019067 as the package
directory. The logical project id remains documented as 2341019067.

## Project analysis

See PROJECT_ANALYSIS.md for a detailed description of architecture, schema,
transactions, and extension ideas.

## Author

Swayam Prakash Sahoo
