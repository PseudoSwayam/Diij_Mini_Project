Swayam_Prakash_Sahoo - Mini Project
=================================

Package: p2341019067 (adjusted from requested numeric package name)
Project name: Swayam_Prakash_Sahoo

Package: 2341019067 (note: numeric package names are invalid in Java; the sources use `p2341019067`)

Overview
--------
This is a compact, runnable Java console application demonstrating an end-to-end JDBC
Library Loan Management System using Apache Derby (embedded).

Structure
---------
- src/p2341019067/ConnectionManager.java
- src/p2341019067/SchemaInitializer.java
- src/p2341019067/BusinessLogic.java
- src/p2341019067/PerformanceEvaluator.java
- src/p2341019067/MainApp.java

Build & Run
-----------
Ensure Derby jars are on your classpath. If Derby is installed and added to PATH, you
can compile and run with (from this folder):

```bash
javac -d out -sourcepath src src/p2341019067/*.java
 Java package identifiers cannot start with a digit; the code therefore uses
 `p2341019067` to reflect the requested numeric id while staying Java-valid.
 The embedded database `SwayamDB` is created in the working directory. The CLI
 offers options to initialize schema, register members, add books, process loans,
 and run basic performance evaluations. The performance runner writes `perf_report.csv`.

Run script
----------
I included `run.sh` which compiles sources into `out/` and runs `p2341019067.MainApp`.
If you have Derby's `derby.jar` path, set the `DERBY_JAR` environment variable before running.

Example:

```bash
export DERBY_JAR=/path/to/derby/lib/derby.jar
./run.sh
```
Notes
-----
- The requested numeric package name `2341019067` is invalid in Java. I used
  `p2341019067` instead to keep the project unique and valid.
- The embedded database `SwayamDB` is created in the working directory. The CLI
  offers options to initialize schema, register members, add books, process loans,
  and run basic performance evaluations. The performance runner writes `perf_report.csv`.
