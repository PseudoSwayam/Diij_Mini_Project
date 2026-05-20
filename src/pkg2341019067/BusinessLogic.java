package pkg2341019067;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class BusinessLogic {
    public static int registerMember(String name) throws SQLException {
        String sql = "INSERT INTO Members(Name, ActiveLoanCount) VALUES (?,0)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }
    public static int addBook(String isbn, String title) throws SQLException {
        String sql = "INSERT INTO Books(ISBN, Title, Available) VALUES (?,?,1)";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, isbn);
            ps.setString(2, title);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return -1;
    }
    // Multi-step operation demonstrating transactions & savepoints
    public static boolean processLoan(int bookId, int memberId) {
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            Savepoint afterBookUpdate = null;
            try (PreparedStatement checkBook = conn.prepareStatement("SELECT Available FROM Books WHERE BookID = ?");
                 PreparedStatement updateBook = conn.prepareStatement("UPDATE Books SET Available = 0 WHERE BookID = ? AND Available = 1");
                 PreparedStatement insertLoan = conn.prepareStatement("INSERT INTO Loans(BookID, MemberID, LoanDate, ReturnDate) VALUES (?,?,?,NULL)", Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement updateMember = conn.prepareStatement("UPDATE Members SET ActiveLoanCount = ActiveLoanCount + 1 WHERE MemberID = ?")) {
                // Verify availability
                checkBook.setInt(1, bookId);
                try (ResultSet rs = checkBook.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Book not found.");
                        conn.rollback();
                        return false;
                    }
                    int avail = rs.getInt(1);
                    if (avail == 0) {
                        System.out.println("Book is not available.");
                        conn.rollback();
                        return false;
                    }
                }
                // Update book availability
                updateBook.setInt(1, bookId);
                int updated = updateBook.executeUpdate();
                if (updated == 0) {
                    System.out.println("Failed to reserve book (concurrent?)");
                    conn.rollback();
                    return false;
                }
                // set savepoint after book update so we can rollback loan insertion if member update fails
                afterBookUpdate = conn.setSavepoint("AfterBookReserve");
                // Insert loan record
                insertLoan.setInt(1, bookId);
                insertLoan.setInt(2, memberId);
                insertLoan.setDate(3, Date.valueOf(LocalDate.now()));
                insertLoan.executeUpdate();
                // Update member count - simulate possible failure
                updateMember.setInt(1, memberId);
                int memUpdated = updateMember.executeUpdate();
                if (memUpdated == 0) {
                    // rollback only the insertion of loan, keep book reserved
                    conn.rollback(afterBookUpdate);
                    conn.commit();
                    System.out.println("Member update failed; rolled back loan insertion, book remains reserved.");
                    return false;
                }
                conn.commit();
                System.out.println("Loan processed successfully.");
                return true;
            } catch (SQLException e) {
                if (afterBookUpdate != null) {
                    try { conn.rollback(); } catch (SQLException ex) {}
                } else {
                    try { conn.rollback(); } catch (SQLException ex) {}
                }
                System.err.println("processLoan failed: " + e.getMessage());
                return false;
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
            return false;
        }
    }
    public static boolean returnBook(int loanId) {
        String getLoan = "SELECT BookID FROM Loans WHERE LoanID = ? AND ReturnDate IS NULL";
        String markReturn = "UPDATE Loans SET ReturnDate = ? WHERE LoanID = ?";
        String markBookAvailable = "UPDATE Books SET Available = 1 WHERE BookID = ?";
        String decMember = "UPDATE Members SET ActiveLoanCount = ActiveLoanCount - 1 WHERE MemberID = ?";
        try (Connection conn = ConnectionManager.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pls = conn.prepareStatement(getLoan);
                 PreparedStatement pr = conn.prepareStatement(markReturn);
                 PreparedStatement pb = conn.prepareStatement(markBookAvailable)) {
                pls.setInt(1, loanId);
                try (ResultSet rs = pls.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Loan not found or already returned.");
                        conn.rollback();
                        return false;
                    }
                    int bookId = rs.getInt(1);
                    pr.setDate(1, Date.valueOf(LocalDate.now()));
                    pr.setInt(2, loanId);
                    pr.executeUpdate();
                    pb.setInt(1, bookId);
                    pb.executeUpdate();
                }
                conn.commit();
                System.out.println("Return processed.");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("returnBook failed: " + e.getMessage());
                return false;
            } finally {
                try { conn.setAutoCommit(true); } catch (SQLException ignore) {}
            }
        } catch (SQLException e) {
            System.err.println("DB error: " + e.getMessage());
            return false;
        }
    }
    public static List<String> queryActiveLoansByMember(int memberId) {
        List<String> out = new ArrayList<>();
        String sql = "SELECT L.LoanID, B.Title, L.LoanDate FROM Loans L JOIN Books B ON L.BookID = B.BookID WHERE L.MemberID = ? AND L.ReturnDate IS NULL";
        try (Connection conn = ConnectionManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(String.format("Loan %d: %s (on %s)", rs.getInt(1), rs.getString(2), rs.getDate(3)));
                }
            }
        } catch (SQLException e) {
            System.err.println("queryActiveLoansByMember error: " + e.getMessage());
        }
        return out;
    }
}
