package pkg2341019067;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Random;
public class PerformanceEvaluator {
    private static long nsToMs(long ns) { return ns / 1_000_000L; }
    public static long insertTest(int count, boolean useBatch) throws SQLException {
        // create many temp rows into Books for test
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            String sql = "INSERT INTO Books(ISBN, Title, Available) VALUES (?,?,1)";
            long start = System.nanoTime();
            if (useBatch) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (int i = 0; i < count; i++) {
                        ps.setString(1, "ISBN-" + i);
                        ps.setString(2, "Bulk Book " + i);
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            } else {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (int i = 0; i < count; i++) {
                        ps.setString(1, "ISBN-" + i);
                        ps.setString(2, "Book " + i);
                        ps.executeUpdate();
                    }
                }
            }
            conn.commit();
            long end = System.nanoTime();
            return nsToMs(end - start);
        }
    }
    public static long queryTestFullScan(int repeats) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection();
             Statement s = conn.createStatement()) {
            long start = System.nanoTime();
            for (int i = 0; i < repeats; i++) {
                try (ResultSet rs = s.executeQuery("SELECT * FROM Loans")) {
                    int c = 0; while (rs.next()) c++;
                }
            }
            long end = System.nanoTime();
            return nsToMs(end - start);
        }
    }
    public static long queryTestIndexedLookup(int memberId, int repeats) throws SQLException {
        String sql = "SELECT * FROM Loans WHERE MemberID = ?";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            long start = System.nanoTime();
            for (int i = 0; i < repeats; i++) {
                ps.setInt(1, memberId);
                try (ResultSet rs = ps.executeQuery()) { while (rs.next()) {} }
            }
            long end = System.nanoTime();
            return nsToMs(end - start);
        }
    }
    public static long statementVsPrepared(int count) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            long startS = System.nanoTime();
            try (Statement s = conn.createStatement()) {
                for (int i = 0; i < count; i++) {
                    String q = "SELECT * FROM Books WHERE ISBN = 'ISBN-" + (i % 100) + "'";
                    try (ResultSet rs = s.executeQuery(q)) { while (rs.next()) {} }
                }
            }
            long endS = System.nanoTime();
            long startP = System.nanoTime();
            try (PreparedStatement ps = conn.prepareStatement("SELECT * FROM Books WHERE ISBN = ?")) {
                for (int i = 0; i < count; i++) {
                    ps.setString(1, "ISBN-" + (i % 100));
                    try (ResultSet rs = ps.executeQuery()) { while (rs.next()) {} }
                }
            }
            long endP = System.nanoTime();
            // Return difference (Statement ms, Prepared ms) written to CSV externally
            return nsToMs(endS - startS) + (nsToMs(endP - startP) << 32);
        }
    }
    public static long transactionGranularityTest(int operations, boolean commitPerOp) throws SQLException {
        try (Connection conn = ConnectionManager.getConnection()) {
            String sql = "INSERT INTO Books(ISBN, Title, Available) VALUES (?, ?, 1)";
            long start = System.nanoTime();
            if (commitPerOp) {
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (int i = 0; i < operations; i++) {
                        ps.setString(1, "TG-" + i);
                        ps.setString(2, "TG Book " + i);
                        ps.executeUpdate();
                        conn.commit();
                    }
                }
            } else {
                conn.setAutoCommit(false);
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (int i = 0; i < operations; i++) {
                        ps.setString(1, "TG-" + i);
                        ps.setString(2, "TG Book " + i);
                        ps.executeUpdate();
                    }
                    conn.commit();
                } finally {
                    conn.setAutoCommit(true);
                }
            }
            long end = System.nanoTime();
            return nsToMs(end - start);
        }
    }
    public static void runAllAndReport() {
        try (PrintWriter out = new PrintWriter(new FileWriter("perf_report.csv", true))) {
            out.println("Test,Param,TimeMs,Notes");
            // warmup
            for (int i = 0; i < 2; i++) insertTest(100, true);
            long t1 = insertTest(1000, false);
            out.println("Insert,1000-plain," + t1 + ",individual inserts");
            long t2 = insertTest(1000, true);
            out.println("Insert,1000-batch," + t2 + ",batch inserts");
            long qfull = queryTestFullScan(5);
            out.println("Query,full-5x," + qfull + ",full table scans");
            long qidx = queryTestIndexedLookup(1, 5);
            out.println("Query,indexed-5x," + qidx + ",indexed lookup");
            long tg1 = transactionGranularityTest(100, true);
            out.println("TxnGranularity,100-commitperop," + tg1 + ",commit per op");
            long tg2 = transactionGranularityTest(100, false);
            out.println("TxnGranularity,100-batched," + tg2 + ",batched commit");
            // Statement vs Prepared
            long both = statementVsPrepared(200);
            long stmtMs = (int)(both & 0xFFFFFFFFL);
            long prepMs = (both >> 32);
            out.println("StatementType,200-Statement," + stmtMs + ",statement concat");
            out.println("StatementType,200-Prepared," + prepMs + ",prepared statements");
            System.out.println("Performance report appended to perf_report.csv");
        } catch (Exception e) {
            System.err.println("Performance run failed: " + e.getMessage());
        }
    }
}
