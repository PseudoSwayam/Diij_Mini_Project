package pkg2341019067;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
public class SchemaInitializer {
    public static void initialize() {
        try (Connection conn = ConnectionManager.getConnection();
             Statement s = conn.createStatement()) {
            // Create Members
            try {
                s.executeUpdate("CREATE TABLE Members (MemberID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, Name VARCHAR(200), ActiveLoanCount INT DEFAULT 0)");
            } catch (SQLException ignore) {
            }
            // Create Books
            try {
                s.executeUpdate("CREATE TABLE Books (BookID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, ISBN VARCHAR(50), Title VARCHAR(255), Available SMALLINT)");
            } catch (SQLException ignore) {
            }
            // Create Loans
            try {
                s.executeUpdate("CREATE TABLE Loans (LoanID INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY, BookID INT, MemberID INT, LoanDate DATE, ReturnDate DATE, CONSTRAINT FK_BOOK FOREIGN KEY (BookID) REFERENCES Books(BookID), CONSTRAINT FK_MEMBER FOREIGN KEY (MemberID) REFERENCES Members(MemberID))");
            } catch (SQLException ignore) {
            }
            // Indexes
            try { s.executeUpdate("CREATE INDEX IDX_BOOK_ISBN ON Books(ISBN)"); } catch (SQLException ignore) {}
            try { s.executeUpdate("CREATE INDEX IDX_LOAN_MEMBER ON Loans(MemberID)"); } catch (SQLException ignore) {}
            try { s.executeUpdate("CREATE INDEX IDX_LOAN_RETURN ON Loans(ReturnDate)"); } catch (SQLException ignore) {}
            // Seed data (simple, idempotent)
            try {
                s.executeUpdate("INSERT INTO Books(ISBN, Title, Available) VALUES ('978-0134685991', 'Effective Java', 1)");
                s.executeUpdate("INSERT INTO Books(ISBN, Title, Available) VALUES ('978-0201633610', 'Design Patterns', 1)");
            } catch (SQLException ignore) {}
            try {
                s.executeUpdate("INSERT INTO Members(Name, ActiveLoanCount) VALUES ('Alice', 0)");
                s.executeUpdate("INSERT INTO Members(Name, ActiveLoanCount) VALUES ('Bob', 0)");
            } catch (SQLException ignore) {}
            System.out.println("Schema initialization attempted (tables created if absent).");
        } catch (SQLException e) {
            System.err.println("Schema initialization failed: " + e.getMessage());
        }
    }
}
