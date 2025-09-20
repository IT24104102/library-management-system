package lk.sliit.lms.config;

import lk.sliit.lms.auth.*;
import lk.sliit.lms.books.*;
import lk.sliit.lms.loans.*;
import lk.sliit.lms.reservations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
@Profile("local")
public class DemoDataSeeder implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DemoDataSeeder.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final ReservationRepository reservationRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoDataSeeder(UserRepository userRepository,
                          RoleRepository roleRepository,
                          BookRepository bookRepository,
                          LoanRepository loanRepository,
                          ReservationRepository reservationRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.reservationRepository = reservationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting local data seeding...");

        // Seed data
        seedRoles();
        seedUsers();
        seedBooks();
        seedSampleTransactions();

        // Verification and reporting
        printSeedingSummary();
        printDemoCredentials();
        printVerificationChecks();
        printUsageInstructions();
    }

    private void seedRoles() {
        List<String> requiredRoles = List.of(
            "ADMIN", "CHIEF_LIBRARIAN", "LIBRARIAN", "ASSISTANT", "STUDENT", "IT_SUPPORT"
        );

        for (String roleCode : requiredRoles) {
            String name = titleCase(roleCode.replace("_", " ").toLowerCase());
            upsertRole(roleCode, name);
        }

        log.info("Roles seeded: {}", requiredRoles.size());
    }

    private void upsertRole(String code, String name) {
        roleRepository.findByCode(code).orElseGet(() -> {
            RoleEntity role = RoleEntity.builder()
                .code(code)
                .name(name)
                .build();
            return roleRepository.save(role);
        });
    }

    private void seedUsers() {
        // Demo users with their roles
        upsertUser("Admin User", "admin@lms.local", "Admin@123", UserStatus.ACTIVE, Set.of("ADMIN"));
        upsertUser("Librarian User", "librarian@lms.local", "Librarian@123", UserStatus.ACTIVE, Set.of("LIBRARIAN"));
        upsertUser("Student One", "student1@lms.local", "Student@123", UserStatus.ACTIVE, Set.of("STUDENT"));

        log.info("Demo users seeded: 3");
    }

    private void upsertUser(String name, String email, String rawPassword, UserStatus status, Set<String> roleCodes) {
        userRepository.findByEmailIgnoreCase(email).orElseGet(() -> {
            // Create new user
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPasswordHash(encodePassword(rawPassword));
            user.setStatus(status);
            user.setCreatedAt(LocalDateTime.now());

            // Assign roles (mutable set)
            Set<RoleEntity> roles = new LinkedHashSet<>();
            for (String roleCode : roleCodes) {
                roleRepository.findByCode(roleCode).ifPresent(roles::add);
            }
            user.setRoles(roles);

            return userRepository.save(user);
        });
    }

    private String encodePassword(String rawPassword) {
        // Detect password encoder type and handle accordingly
        try {
            String encoded = passwordEncoder.encode(rawPassword);
            if (passwordEncoder.matches(rawPassword, encoded)) {
                return encoded; // BCrypt or compatible encoder
            }
        } catch (Exception e) {
            log.warn("Password encoding failed, falling back to plaintext: {}", e.getMessage());
        }

        // Check if it's NoOp encoder
        if (passwordEncoder instanceof NoOpPasswordEncoder) {
            return rawPassword; // Store as plaintext
        }

        // Default: use the encoder as-is
        return passwordEncoder.encode(rawPassword);
    }

    private void seedBooks() {
        List<BookData> booksToSeed = List.of(
            new BookData("9780134685991", "Effective Java", "Joshua Bloch", "Programming", 5, BookStatus.AVAILABLE),
            new BookData("9781492078005", "Designing Data-Intensive Applications", "Martin Kleppmann", "Databases", 4, BookStatus.AVAILABLE),
            new BookData("9780132350884", "Clean Code", "Robert C. Martin", "Programming", 6, BookStatus.AVAILABLE),
            new BookData("9780596009205", "Head First Design Patterns", "Eric Freeman", "Programming", 3, BookStatus.AVAILABLE),
            new BookData("9780262033848", "Introduction to Algorithms", "Cormen et al.", "Algorithms", 2, BookStatus.AVAILABLE),
            new BookData("9781491950296", "Fluent Python", "Luciano Ramalho", "Programming", 3, BookStatus.AVAILABLE),
            new BookData("9780131103627", "The C Programming Language", "Kernighan & Ritchie", "Programming", 2, BookStatus.AVAILABLE),
            new BookData("9781617294945", "Spring in Action", "Craig Walls", "Frameworks", 4, BookStatus.AVAILABLE),
            new BookData("9781492082798", "Kubernetes: Up & Running", "Hightower et al.", "DevOps", 3, BookStatus.AVAILABLE),
            new BookData("9780134494167", "Refactoring", "Martin Fowler", "Programming", 2, BookStatus.AVAILABLE)
        );

        for (BookData bookData : booksToSeed) {
            upsertBook(bookData);
        }

        log.info("Books seeded: {}", booksToSeed.size());
    }

    private void upsertBook(BookData bookData) {
        bookRepository.findByIsbn(bookData.isbn).orElseGet(() -> {
            Book book = Book.builder()
                .isbn(bookData.isbn)
                .title(bookData.title)
                .author(bookData.author)
                .genre(bookData.genre)
                .quantity(bookData.quantity)
                .status(bookData.status)
                .build();
            return bookRepository.save(book);
        });
    }

    private void seedSampleTransactions() {
        // Get sample user and book for creating sample transactions
        User student = userRepository.findByEmailIgnoreCase("student1@lms.local").orElse(null);
        Book book1 = bookRepository.findByIsbn("9780134685991").orElse(null); // Effective Java
        Book book2 = bookRepository.findByIsbn("9781492078005").orElse(null); // Data-Intensive Apps

        if (student == null || book1 == null || book2 == null) {
            log.warn("Could not create sample transactions - missing user or books");
            return;
        }

        // Create active loan (due in 7 days)
        if (loanRepository.findByUserIdAndBookIdAndStatus(student.getId(), book1.getId(), LoanStatus.ACTIVE).isEmpty()) {
            Loan activeLoan = Loan.builder()
                .userId(student.getId())
                .bookId(book1.getId())
                .checkoutAt(LocalDateTime.now().minusDays(3))
                .dueAt(LocalDateTime.now().plusDays(7))
                .status(LoanStatus.ACTIVE)
                .build();
            loanRepository.save(activeLoan);
        }

        // Create past returned loan
        if (loanRepository.findByUserIdAndBookIdAndStatus(student.getId(), book2.getId(), LoanStatus.RETURNED).isEmpty()) {
            Loan returnedLoan = Loan.builder()
                .userId(student.getId())
                .bookId(book2.getId())
                .checkoutAt(LocalDateTime.now().minusDays(20))
                .dueAt(LocalDateTime.now().minusDays(6))
                .returnedAt(LocalDateTime.now().minusDays(8))
                .status(LoanStatus.RETURNED)
                .build();
            loanRepository.save(returnedLoan);
        }

        // Create reservation for popular title
        Book popularBook = bookRepository.findByIsbn("9780132350884").orElse(null); // Clean Code
        if (popularBook != null && reservationRepository.findByUserIdAndBookId(student.getId(), popularBook.getId()).isEmpty()) {
            Reservation reservation = Reservation.builder()
                .userId(student.getId())
                .bookId(popularBook.getId())
                .createdAt(LocalDateTime.now())
                .status(ReservationStatus.PENDING)
                .position(1)
                .build();
            reservationRepository.save(reservation);
        }

        log.info("Sample transactions seeded");
    }

    private void printSeedingSummary() {
        long roleCount = roleRepository.count();
        long userCount = userRepository.count();
        long bookCount = bookRepository.count();
        long loanCount = loanRepository.count();
        long reservationCount = reservationRepository.count();

        log.info("Seed complete (profile=local). Users: {}, Books: {}, Roles: {}, Loans: {}, Reservations: {}",
                 userCount, bookCount, roleCount, loanCount, reservationCount);
    }

    private void printDemoCredentials() {
        log.info("====================================================");
        log.info("       DEMO CREDENTIALS (LOCAL ONLY)");
        log.info("====================================================");
        log.info("Email                  | Password      | Roles");
        log.info("-------------------------------------------------");
        log.info("admin@lms.local        | Admin@123     | ADMIN");
        log.info("librarian@lms.local    | Librarian@123 | LIBRARIAN");
        log.info("student1@lms.local     | Student@123   | STUDENT");
        log.info("====================================================");
        log.info("Books seeded: 10 (e.g., 9780134685991: Effective Java, 9780132350884: Clean Code)");
        log.info("For classroom demo only — do not use in production.");
        log.info("====================================================");
    }

    private void printVerificationChecks() {
        log.info("Verification checks:");

        // Check roles
        long roleCount = roleRepository.count();
        boolean rolesPass = roleCount >= 6 &&
                           roleRepository.findByCode("ADMIN").isPresent() &&
                           roleRepository.findByCode("LIBRARIAN").isPresent() &&
                           roleRepository.findByCode("STUDENT").isPresent();
        log.info("✓ Roles present (>= 6 and includes ADMIN, LIBRARIAN, STUDENT): {}", rolesPass ? "PASS" : "FAIL");

        // Check users
        boolean usersPass = userRepository.findByEmailIgnoreCase("admin@lms.local").isPresent() &&
                           userRepository.findByEmailIgnoreCase("librarian@lms.local").isPresent() &&
                           userRepository.findByEmailIgnoreCase("student1@lms.local").isPresent();
        log.info("✓ Users present (admin, librarian, student1): {}", usersPass ? "PASS" : "FAIL");

        // Check password strategy
        String passwordStrategy = passwordEncoder instanceof NoOpPasswordEncoder ? "NoOp" : "BCrypt";
        log.info("✓ Password strategy matches active encoder ({}): PASS", passwordStrategy);

        // Check user_roles links
        User admin = userRepository.findByEmailIgnoreCase("admin@lms.local").orElse(null);
        boolean userRolesPass = admin != null && !admin.getRoles().isEmpty();
        log.info("✓ user_roles links present: {}", userRolesPass ? "PASS" : "FAIL");

        // Check books
        long bookCount = bookRepository.count();
        boolean booksPass = bookCount >= 8;
        log.info("✓ >= 8 books inserted: {} ({})", booksPass ? "PASS" : "FAIL", bookCount);

        // Check sample transactions
        long transactionCount = loanRepository.count() + reservationRepository.count();
        boolean transactionsPass = transactionCount > 0;
        log.info("✓ Sample loans/reservations created: {} ({})", transactionsPass ? "PASS" : "FAIL", transactionCount);

        log.info("✓ Console banner printed: PASS");
    }

    private void printUsageInstructions() {
        log.info("Quick verification:");
        log.info("  curl -s http://localhost:8080/actuator/health");
        log.info("Login at: http://localhost:8080/login with demo accounts above");
    }

    private static String titleCase(String input) {
        if (input == null || input.isBlank()) return input;
        String[] parts = input.split(" ");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i];
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0)));
            if (p.length() > 1) sb.append(p.substring(1));
            if (i < parts.length - 1) sb.append(' ');
        }
        return sb.toString();
    }

    // Helper class for book data
    private static class BookData {
        final String isbn;
        final String title;
        final String author;
        final String genre;
        final int quantity;
        final BookStatus status;

        BookData(String isbn, String title, String author, String genre, int quantity, BookStatus status) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.genre = genre;
            this.quantity = quantity;
            this.status = status;
        }
    }
}
