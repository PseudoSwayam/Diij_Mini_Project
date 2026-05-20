package pkg2341019067;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
public class MainApp {
    public static void main(String[] args) {
        System.out.println("Swayam_Prakash_Sahoo - Mini JDBC Library (Derby)");
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {
            System.out.println("\nMenu:\n1) Init schema\n2) Register member\n3) Add book\n4) Process loan\n5) Return book\n6) Query active loans by member\n7) Run performance evaluation\n8) Shutdown DB & exit\nChoose:");
            String opt = scanner.nextLine().trim();
            try {
                switch (opt) {
                    case "1":
                        SchemaInitializer.initialize();
                        break;
                    case "2":
                        System.out.print("Member name: ");
                        String name = scanner.nextLine();
                        int memId = BusinessLogic.registerMember(name);
                        System.out.println("Member created id=" + memId);
                        break;
                    case "3":
                        System.out.print("ISBN: ");
                        String isbn = scanner.nextLine();
                        System.out.print("Title: ");
                        String title = scanner.nextLine();
                        int bid = BusinessLogic.addBook(isbn, title);
                        System.out.println("Book added id=" + bid);
                        break;
                    case "4":
                        System.out.print("BookID: ");
                        int bookId = Integer.parseInt(scanner.nextLine());
                        System.out.print("MemberID: ");
                        int memberId = Integer.parseInt(scanner.nextLine());
                        BusinessLogic.processLoan(bookId, memberId);
                        break;
                    case "5":
                        System.out.print("LoanID: ");
                        int loanId = Integer.parseInt(scanner.nextLine());
                        BusinessLogic.returnBook(loanId);
                        break;
                    case "6":
                        System.out.print("MemberID: ");
                        int m = Integer.parseInt(scanner.nextLine());
                        List<String> loans = BusinessLogic.queryActiveLoansByMember(m);
                        loans.forEach(System.out::println);
                        break;
                    case "7":
                        System.out.println("Running performance evaluation (may take a while)...");
                        PerformanceEvaluator.runAllAndReport();
                        break;
                    case "8":
                        System.out.println("Shutting down DB and exiting.");
                        ConnectionManager.shutdown();
                        running = false;
                        break;
                    default:
                        System.out.println("Unknown option.");
                }
            } catch (SQLException e) {
                System.err.println("SQL error: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        scanner.close();
    }
}
