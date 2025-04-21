/**
 * Library Management System
 * A JDBC-based project that performs CRUD operations for a library
 */

// Database schema:
/*
CREATE TABLE books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(20) UNIQUE,
    publication_year INT,
    copies_available INT DEFAULT 0
);

CREATE TABLE patrons (
    patron_id INT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone VARCHAR(20),
    registration_date DATE
);

CREATE TABLE borrowing_records (
    record_id INT PRIMARY KEY AUTO_INCREMENT,
    book_id INT,
    patron_id INT,
    borrow_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE,
    FOREIGN KEY (book_id) REFERENCES books(book_id),
    FOREIGN KEY (patron_id) REFERENCES patrons(patron_id)
);
*/

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";
    
    // Main method to run the application
    public static void main(String[] args) {
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create database connection
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connected to database successfully!");
            
            // Initialize services
            BookService bookService = new BookService(connection);
            PatronService patronService = new PatronService(connection);
            BorrowingService borrowingService = new BorrowingService(connection);
            
            // Display menu and handle user input
            displayMenu(bookService, patronService, borrowingService);
            
            // Close connection
            connection.close();
            System.out.println("Connection closed.");
        } catch (ClassNotFoundException e) {
            System.out.println("JDBC Driver not found: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }
    
    private static void displayMenu(BookService bookService, PatronService patronService, BorrowingService borrowingService) {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== LIBRARY MANAGEMENT SYSTEM =====");
            System.out.println("1. Book Management");
            System.out.println("2. Patron Management");
            System.out.println("3. Borrowing Management");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    handleBookManagement(scanner, bookService);
                    break;
                case 2:
                    handlePatronManagement(scanner, patronService);
                    break;
                case 3:
                    handleBorrowingManagement(scanner, borrowingService, bookService, patronService);
                    break;
                case 0:
                    running = false;
                    System.out.println("Exiting the application...");
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private static void handleBookManagement(Scanner scanner, BookService bookService) {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== BOOK MANAGEMENT =====");
            System.out.println("1. Add a new book");
            System.out.println("2. Find a book by ID");
            System.out.println("3. Find books by title");
            System.out.println("4. Update book information");
            System.out.println("5. Delete a book");
            System.out.println("6. List all books");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    addBook(scanner, bookService);
                    break;
                case 2:
                    findBookById(scanner, bookService);
                    break;
                case 3:
                    findBooksByTitle(scanner, bookService);
                    break;
                case 4:
                    updateBook(scanner, bookService);
                    break;
                case 5:
                    deleteBook(scanner, bookService);
                    break;
                case 6:
                    listAllBooks(bookService);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void handlePatronManagement(Scanner scanner, PatronService patronService) {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== PATRON MANAGEMENT =====");
            System.out.println("1. Register a new patron");
            System.out.println("2. Find a patron by ID");
            System.out.println("3. Find patrons by name");
            System.out.println("4. Update patron information");
            System.out.println("5. Delete a patron");
            System.out.println("6. List all patrons");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    registerPatron(scanner, patronService);
                    break;
                case 2:
                    findPatronById(scanner, patronService);
                    break;
                case 3:
                    findPatronsByName(scanner, patronService);
                    break;
                case 4:
                    updatePatron(scanner, patronService);
                    break;
                case 5:
                    deletePatron(scanner, patronService);
                    break;
                case 6:
                    listAllPatrons(patronService);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void handleBorrowingManagement(Scanner scanner, BorrowingService borrowingService, BookService bookService, PatronService patronService) {
        boolean running = true;
        
        while (running) {
            System.out.println("\n===== BORROWING MANAGEMENT =====");
            System.out.println("1. Borrow a book");
            System.out.println("2. Return a book");
            System.out.println("3. View borrowing record");
            System.out.println("4. List all active borrowings");
            System.out.println("5. List patron's borrowing history");
            System.out.println("0. Back to main menu");
            System.out.print("Enter your choice: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    borrowBook(scanner, borrowingService, bookService, patronService);
                    break;
                case 2:
                    returnBook(scanner, borrowingService);
                    break;
                case 3:
                    viewBorrowingRecord(scanner, borrowingService);
                    break;
                case 4:
                    listActiveBorrowings(borrowingService);
                    break;
                case 5:
                    viewPatronBorrowingHistory(scanner, borrowingService, patronService);
                    break;
                case 0:
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    // Book management methods
    private static void addBook(Scanner scanner, BookService bookService) {
        System.out.println("\n----- Add a New Book -----");
        
        System.out.print("Enter title: ");
        String title = scanner.nextLine();
        
        System.out.print("Enter author: ");
        String author = scanner.nextLine();
        
        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine();
        
        System.out.print("Enter publication year: ");
        int publicationYear = scanner.nextInt();
        
        System.out.print("Enter number of copies available: ");
        int copiesAvailable = scanner.nextInt();
        
        Book book = new Book(0, title, author, isbn, publicationYear, copiesAvailable);
        
        try {
            int bookId = bookService.addBook(book);
            System.out.println("Book added successfully with ID: " + bookId);
        } catch (SQLException e) {
            System.out.println("Failed to add book: " + e.getMessage());
        }
    }
    
    private static void findBookById(Scanner scanner, BookService bookService) {
        System.out.println("\n----- Find Book by ID -----");
        
        System.out.print("Enter book ID: ");
        int bookId = scanner.nextInt();
        
        try {
            Book book = bookService.getBookById(bookId);
            if (book != null) {
                System.out.println(book);
            } else {
                System.out.println("Book not found with ID: " + bookId);
            }
        } catch (SQLException e) {
            System.out.println("Error finding book: " + e.getMessage());
        }
    }
    
    private static void findBooksByTitle(Scanner scanner, BookService bookService) {
        System.out.println("\n----- Find Books by Title -----");
        
        System.out.print("Enter title (or part of title): ");
        String title = scanner.nextLine();
        
        try {
            List<Book> books = bookService.getBooksByTitle(title);
            if (!books.isEmpty()) {
                System.out.println("Found " + books.size() + " book(s):");
                for (Book book : books) {
                    System.out.println(book);
                }
            } else {
                System.out.println("No books found with title containing: " + title);
            }
        } catch (SQLException e) {
            System.out.println("Error finding books: " + e.getMessage());
        }
    }
    
    private static void updateBook(Scanner scanner, BookService bookService) {
        System.out.println("\n----- Update Book Information -----");
        
        System.out.print("Enter book ID to update: ");
        int bookId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        try {
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                System.out.println("Book not found with ID: " + bookId);
                return;
            }
            
            System.out.println("Current book details: " + book);
            
            System.out.print("Enter new title (or press Enter to keep current): ");
            String title = scanner.nextLine();
            if (!title.isEmpty()) {
                book.setTitle(title);
            }
            
            System.out.print("Enter new author (or press Enter to keep current): ");
            String author = scanner.nextLine();
            if (!author.isEmpty()) {
                book.setAuthor(author);
            }
            
            System.out.print("Enter new ISBN (or press Enter to keep current): ");
            String isbn = scanner.nextLine();
            if (!isbn.isEmpty()) {
                book.setIsbn(isbn);
            }
            
            System.out.print("Enter new publication year (or 0 to keep current): ");
            int year = scanner.nextInt();
            if (year != 0) {
                book.setPublicationYear(year);
            }
            
            System.out.print("Enter new number of copies (or -1 to keep current): ");
            int copies = scanner.nextInt();
            if (copies != -1) {
                book.setCopiesAvailable(copies);
            }
            
            boolean success = bookService.updateBook(book);
            if (success) {
                System.out.println("Book updated successfully!");
            } else {
                System.out.println("Failed to update book.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }
    
    private static void deleteBook(Scanner scanner, BookService bookService) {
        System.out.println("\n----- Delete a Book -----");
        
        System.out.print("Enter book ID to delete: ");
        int bookId = scanner.nextInt();
        
        try {
            boolean success = bookService.deleteBook(bookId);
            if (success) {
                System.out.println("Book deleted successfully!");
            } else {
                System.out.println("Failed to delete book. It may be referenced by borrowing records.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }
    
    private static void listAllBooks(BookService bookService) {
        System.out.println("\n----- All Books -----");
        
        try {
            List<Book> books = bookService.getAllBooks();
            if (!books.isEmpty()) {
                for (Book book : books) {
                    System.out.println(book);
                }
                System.out.println("Total books: " + books.size());
            } else {
                System.out.println("No books found in the database.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving books: " + e.getMessage());
        }
    }
    
    // Patron management methods
    private static void registerPatron(Scanner scanner, PatronService patronService) {
        System.out.println("\n----- Register a New Patron -----");
        
        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine();
        
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine();
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        
        Patron patron = new Patron(0, firstName, lastName, email, phone, LocalDate.now());
        
        try {
            int patronId = patronService.addPatron(patron);
            System.out.println("Patron registered successfully with ID: " + patronId);
        } catch (SQLException e) {
            System.out.println("Failed to register patron: " + e.getMessage());
        }
    }
    
    private static void findPatronById(Scanner scanner, PatronService patronService) {
        System.out.println("\n----- Find Patron by ID -----");
        
        System.out.print("Enter patron ID: ");
        int patronId = scanner.nextInt();
        
        try {
            Patron patron = patronService.getPatronById(patronId);
            if (patron != null) {
                System.out.println(patron);
            } else {
                System.out.println("Patron not found with ID: " + patronId);
            }
        } catch (SQLException e) {
            System.out.println("Error finding patron: " + e.getMessage());
        }
    }
    
    private static void findPatronsByName(Scanner scanner, PatronService patronService) {
        System.out.println("\n----- Find Patrons by Name -----");
        
        System.out.print("Enter name (or part of name): ");
        String name = scanner.nextLine();
        
        try {
            List<Patron> patrons = patronService.getPatronsByName(name);
            if (!patrons.isEmpty()) {
                System.out.println("Found " + patrons.size() + " patron(s):");
                for (Patron patron : patrons) {
                    System.out.println(patron);
                }
            } else {
                System.out.println("No patrons found with name containing: " + name);
            }
        } catch (SQLException e) {
            System.out.println("Error finding patrons: " + e.getMessage());
        }
    }
    
    private static void updatePatron(Scanner scanner, PatronService patronService) {
        System.out.println("\n----- Update Patron Information -----");
        
        System.out.print("Enter patron ID to update: ");
        int patronId = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        try {
            Patron patron = patronService.getPatronById(patronId);
            if (patron == null) {
                System.out.println("Patron not found with ID: " + patronId);
                return;
            }
            
            System.out.println("Current patron details: " + patron);
            
            System.out.print("Enter new first name (or press Enter to keep current): ");
            String firstName = scanner.nextLine();
            if (!firstName.isEmpty()) {
                patron.setFirstName(firstName);
            }
            
            System.out.print("Enter new last name (or press Enter to keep current): ");
            String lastName = scanner.nextLine();
            if (!lastName.isEmpty()) {
                patron.setLastName(lastName);
            }
            
            System.out.print("Enter new email (or press Enter to keep current): ");
            String email = scanner.nextLine();
            if (!email.isEmpty()) {
                patron.setEmail(email);
            }
            
            System.out.print("Enter new phone (or press Enter to keep current): ");
            String phone = scanner.nextLine();
            if (!phone.isEmpty()) {
                patron.setPhone(phone);
            }
            
            boolean success = patronService.updatePatron(patron);
            if (success) {
                System.out.println("Patron updated successfully!");
            } else {
                System.out.println("Failed to update patron.");
            }
        } catch (SQLException e) {
            System.out.println("Error updating patron: " + e.getMessage());
        }
    }
    
    private static void deletePatron(Scanner scanner, PatronService patronService) {
        System.out.println("\n----- Delete a Patron -----");
        
        System.out.print("Enter patron ID to delete: ");
        int patronId = scanner.nextInt();
        
        try {
            boolean success = patronService.deletePatron(patronId);
            if (success) {
                System.out.println("Patron deleted successfully!");
            } else {
                System.out.println("Failed to delete patron. They may have active borrowings.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting patron: " + e.getMessage());
        }
    }
    
    private static void listAllPatrons(PatronService patronService) {
        System.out.println("\n----- All Patrons -----");
        
        try {
            List<Patron> patrons = patronService.getAllPatrons();
            if (!patrons.isEmpty()) {
                for (Patron patron : patrons) {
                    System.out.println(patron);
                }
                System.out.println("Total patrons: " + patrons.size());
            } else {
                System.out.println("No patrons found in the database.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving patrons: " + e.getMessage());
        }
    }
    
    // Borrowing management methods
    private static void borrowBook(Scanner scanner, BorrowingService borrowingService, 
                                  BookService bookService, PatronService patronService) {
        System.out.println("\n----- Borrow a Book -----");
        
        System.out.print("Enter patron ID: ");
        int patronId = scanner.nextInt();
        
        System.out.print("Enter book ID: ");
        int bookId = scanner.nextInt();
        
        try {
            // Check if patron exists
            Patron patron = patronService.getPatronById(patronId);
            if (patron == null) {
                System.out.println("Patron not found with ID: " + patronId);
                return;
            }
            
            // Check if book exists and has copies available
            Book book = bookService.getBookById(bookId);
            if (book == null) {
                System.out.println("Book not found with ID: " + bookId);
                return;
            }
            
            if (book.getCopiesAvailable() <= 0) {
                System.out.println("No copies available for borrowing. Please try later.");
                return;
            }
            
            // Calculate due date (default: 2 weeks from today)
            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusWeeks(2);
            
            // Create borrowing record
            BorrowingRecord record = new BorrowingRecord(0, bookId, patronId, borrowDate, dueDate, null);
            
            int recordId = borrowingService.borrowBook(record);
            if (recordId > 0) {
                // Update book copies available
                book.setCopiesAvailable(book.getCopiesAvailable() - 1);
                bookService.updateBook(book);
                
                System.out.println("Book borrowed successfully!");
                System.out.println("Due date: " + dueDate);
            } else {
                System.out.println("Failed to borrow book.");
            }
        } catch (SQLException e) {
            System.out.println("Error processing borrowing: " + e.getMessage());
        }
    }
    
    private static void returnBook(Scanner scanner, BorrowingService borrowingService) {
        System.out.println("\n----- Return a Book -----");
        
        System.out.print("Enter borrowing record ID: ");
        int recordId = scanner.nextInt();
        
        try {
            boolean success = borrowingService.returnBook(recordId);
            if (success) {
                System.out.println("Book returned successfully!");
            } else {
                System.out.println("Failed to return book. Record may not exist or book may already be returned.");
            }
        } catch (SQLException e) {
            System.out.println("Error processing return: " + e.getMessage());
        }
    }
    
    private static void viewBorrowingRecord(Scanner scanner, BorrowingService borrowingService) {
        System.out.println("\n----- View Borrowing Record -----");
        
        System.out.print("Enter borrowing record ID: ");
        int recordId = scanner.nextInt();
        
        try {
            BorrowingRecord record = borrowingService.getBorrowingRecordById(recordId);
            if (record != null) {
                System.out.println(record);
                
                // Check if the book is overdue
                if (record.getReturnDate() == null && record.getDueDate().isBefore(LocalDate.now())) {
                    System.out.println("STATUS: OVERDUE");
                    long daysOverdue = record.getDueDate().until(LocalDate.now()).getDays();
                    System.out.println("Days overdue: " + daysOverdue);
                } else if (record.getReturnDate() == null) {
                    System.out.println("STATUS: ACTIVE");
                    long daysRemaining = LocalDate.now().until(record.getDueDate()).getDays();
                    System.out.println("Days remaining: " + daysRemaining);
                } else {
                    System.out.println("STATUS: RETURNED");
                }
            } else {
                System.out.println("Borrowing record not found with ID: " + recordId);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving borrowing record: " + e.getMessage());
        }
    }
    
    private static void listActiveBorrowings(BorrowingService borrowingService) {
        System.out.println("\n----- Active Borrowings -----");
        
        try {
            List<BorrowingRecord> records = borrowingService.getActiveBorrowings();
            if (!records.isEmpty()) {
                for (BorrowingRecord record : records) {
                    System.out.println(record);
                    
                    if (record.getDueDate().isBefore(LocalDate.now())) {
                        System.out.println("STATUS: OVERDUE");
                        long daysOverdue = record.getDueDate().until(LocalDate.now()).getDays();
                        System.out.println("Days overdue: " + daysOverdue);
                    } else {
                        System.out.println("STATUS: ACTIVE");
                        long daysRemaining = LocalDate.now().until(record.getDueDate()).getDays();
                        System.out.println("Days remaining: " + daysRemaining);
                    }
                    System.out.println("---------------------------");
                }
                System.out.println("Total active borrowings: " + records.size());
            } else {
                System.out.println("No active borrowings found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving active borrowings: " + e.getMessage());
        }
    }
    
    private static void viewPatronBorrowingHistory(Scanner scanner, BorrowingService borrowingService, PatronService patronService) {
        System.out.println("\n----- Patron's Borrowing History -----");
        
        System.out.print("Enter patron ID: ");
        int patronId = scanner.nextInt();
        
        try {
            Patron patron = patronService.getPatronById(patronId);
            if (patron == null) {
                System.out.println("Patron not found with ID: " + patronId);
                return;
            }
            
            List<BorrowingRecord> records = borrowingService.getPatronBorrowingHistory(patronId);
            if (!records.isEmpty()) {
                System.out.println("Borrowing history for " + patron.getFirstName() + " " + patron.getLastName() + ":");
                for (BorrowingRecord record : records) {
                    System.out.println(record);
                    
                    if (record.getReturnDate() == null && record.getDueDate().isBefore(LocalDate.now())) {
                        System.out.println("STATUS: OVERDUE");
                    } else if (record.getReturnDate() == null) {
                        System.out.println("STATUS: ACTIVE");
                    } else {
                        System.out.println("STATUS: RETURNED on " + record.getReturnDate());
                    }
                    System.out.println("---------------------------");
                }
                System.out.println("Total records: " + records.size());
            } else {
                System.out.println("No borrowing history found for this patron.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving borrowing history: " + e.getMessage());
        }
    }
}

// Model classes
class Book {
    private int bookId;
    private String title;
    private String author;
    private String isbn;
    private int publicationYear;
    private int copiesAvailable;
    
    public Book(int bookId, String title, String author, String isbn, int publicationYear, int copiesAvailable) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publicationYear = publicationYear;
        this.copiesAvailable = copiesAvailable;
    }
    
    // Getters and setters
    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    
    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }
    
    public int getCopiesAvailable() { return copiesAvailable; }
    public void setCopiesAvailable(int copiesAvailable) { this.copiesAvailable = copiesAvailable; }
    
    @Override
    public String toString() {
        return "Book ID: " + bookId + " | Title: " + title + " | Author: " + author + 
               " | ISBN: " + isbn + " | Year: " + publicationYear + " | Copies: " + copiesAvailable;
    }
}

class Patron {
    private int patronId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private LocalDate registrationDate;
    
    public Patron(int patronId, String firstName, String lastName, String email, String phone, LocalDate registrationDate) {
        this.patronId = patronId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.registrationDate = registrationDate;
    }
    
    // Getters and setters
    public int getPatronId() { return patronId; }
    public void setPatronId(int patronId) { this.patronId = patronId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }
    
    @Override
    public String toString() {
        return "Patron ID: " + patronId + " | Name: " + firstName + " " + lastName + 
               " | Email: " + email + " | Phone: " + phone + " | Registered: " + registrationDate;
    }
}

class BorrowingRecord {
    private int recordId;
    private int bookId;
    private int patronId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    
    public BorrowingRecord(int recordId, int bookId, int patronId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        this.recordId = recordId;
        this.bookId = bookId;
        this.patronId = patronId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }
    
    // Getters and setters
    public int getRecordId() { return recordId; }
    public void setRecordId(int recordId) { this.recordId = recordId; }

    public int getBookId() { return bookId; }
    public void setBookId(int bookId) { this.bookId = bookId; }
    
    public int getPatronId() { return patronId; }
    public void setPatronId(int patronId) { this.patronId = patronId; }
    
    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }
    
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    
    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
    
    @Override
    public String toString() {
        return "Record ID: " + recordId + " | Book ID: " + bookId + " | Patron ID: " + patronId + 
               " | Borrow Date: " + borrowDate + " | Due Date: " + dueDate + 
               " | Return Date: " + (returnDate != null ? returnDate : "Not returned");
    }
}

// Service classes
class BookService {
    private Connection connection;
    
    public BookService(Connection connection) {
        this.connection = connection;
    }
    
    // Create
    public int addBook(Book book) throws SQLException {
        String sql = "INSERT INTO books (title, author, isbn, publication_year, copies_available) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setInt(4, book.getPublicationYear());
            statement.setInt(5, book.getCopiesAvailable());
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating book failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int bookId = generatedKeys.getInt(1);
                    book.setBookId(bookId);
                    return bookId;
                } else {
                    throw new SQLException("Creating book failed, no ID obtained.");
                }
            }
        }
    }
    
    // Read
    public Book getBookById(int bookId) throws SQLException {
        String sql = "SELECT * FROM books WHERE book_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractBookFromResultSet(resultSet);
                }
            }
        }
        
        return null;
    }
    
    public List<Book> getBooksByTitle(String title) throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE title LIKE ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + title + "%");
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    books.add(extractBookFromResultSet(resultSet));
                }
            }
        }
        
        return books;
    }
    
    public List<Book> getAllBooks() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                books.add(extractBookFromResultSet(resultSet));
            }
        }
        
        return books;
    }
    
    // Update
    public boolean updateBook(Book book) throws SQLException {
        String sql = "UPDATE books SET title = ?, author = ?, isbn = ?, publication_year = ?, copies_available = ? WHERE book_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getIsbn());
            statement.setInt(4, book.getPublicationYear());
            statement.setInt(5, book.getCopiesAvailable());
            statement.setInt(6, book.getBookId());
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Delete
    public boolean deleteBook(int bookId) throws SQLException {
        String sql = "DELETE FROM books WHERE book_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, bookId);
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Helper method
    private Book extractBookFromResultSet(ResultSet resultSet) throws SQLException {
        int bookId = resultSet.getInt("book_id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        String isbn = resultSet.getString("isbn");
        int publicationYear = resultSet.getInt("publication_year");
        int copiesAvailable = resultSet.getInt("copies_available");
        
        return new Book(bookId, title, author, isbn, publicationYear, copiesAvailable);
    }
}

class PatronService {
    private Connection connection;
    
    public PatronService(Connection connection) {
        this.connection = connection;
    }
    
    // Create
    public int addPatron(Patron patron) throws SQLException {
        String sql = "INSERT INTO patrons (first_name, last_name, email, phone, registration_date) VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, patron.getFirstName());
            statement.setString(2, patron.getLastName());
            statement.setString(3, patron.getEmail());
            statement.setString(4, patron.getPhone());
            statement.setDate(5, Date.valueOf(patron.getRegistrationDate()));
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating patron failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int patronId = generatedKeys.getInt(1);
                    patron.setPatronId(patronId);
                    return patronId;
                } else {
                    throw new SQLException("Creating patron failed, no ID obtained.");
                }
            }
        }
    }
    
    // Read
    public Patron getPatronById(int patronId) throws SQLException {
        String sql = "SELECT * FROM patrons WHERE patron_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, patronId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractPatronFromResultSet(resultSet);
                }
            }
        }
        
        return null;
    }
    
    public List<Patron> getPatronsByName(String name) throws SQLException {
        List<Patron> patrons = new ArrayList<>();
        String sql = "SELECT * FROM patrons WHERE first_name LIKE ? OR last_name LIKE ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            statement.setString(2, "%" + name + "%");
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    patrons.add(extractPatronFromResultSet(resultSet));
                }
            }
        }
        
        return patrons;
    }
    
    public List<Patron> getAllPatrons() throws SQLException {
        List<Patron> patrons = new ArrayList<>();
        String sql = "SELECT * FROM patrons";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                patrons.add(extractPatronFromResultSet(resultSet));
            }
        }
        
        return patrons;
    }
    
    // Update
    public boolean updatePatron(Patron patron) throws SQLException {
        String sql = "UPDATE patrons SET first_name = ?, last_name = ?, email = ?, phone = ? WHERE patron_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, patron.getFirstName());
            statement.setString(2, patron.getLastName());
            statement.setString(3, patron.getEmail());
            statement.setString(4, patron.getPhone());
            statement.setInt(5, patron.getPatronId());
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Delete
    public boolean deletePatron(int patronId) throws SQLException {
        String sql = "DELETE FROM patrons WHERE patron_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, patronId);
            
            int affectedRows = statement.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    // Helper method
    private Patron extractPatronFromResultSet(ResultSet resultSet) throws SQLException {
        int patronId = resultSet.getInt("patron_id");
        String firstName = resultSet.getString("first_name");
        String lastName = resultSet.getString("last_name");
        String email = resultSet.getString("email");
        String phone = resultSet.getString("phone");
        LocalDate registrationDate = resultSet.getDate("registration_date").toLocalDate();
        
        return new Patron(patronId, firstName, lastName, email, phone, registrationDate);
    }
}

class BorrowingService {
    private Connection connection;
    
    public BorrowingService(Connection connection) {
        this.connection = connection;
    }
    
    // Create - Borrow a book
    public int borrowBook(BorrowingRecord record) throws SQLException {
        String sql = "INSERT INTO borrowing_records (book_id, patron_id, borrow_date, due_date) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, record.getBookId());
            statement.setInt(2, record.getPatronId());
            statement.setDate(3, Date.valueOf(record.getBorrowDate()));
            statement.setDate(4, Date.valueOf(record.getDueDate()));
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating borrowing record failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int recordId = generatedKeys.getInt(1);
                    record.setRecordId(recordId);
                    return recordId;
                } else {
                    throw new SQLException("Creating borrowing record failed, no ID obtained.");
                }
            }
        }
    }
    
    // Update - Return a book
    public boolean returnBook(int recordId) throws SQLException {
        // First, get the borrowing record
        BorrowingRecord record = getBorrowingRecordById(recordId);
        if (record == null || record.getReturnDate() != null) {
            return false; // Record doesn't exist or book already returned
        }
        
        // Update the borrowing record with return date
        String updateSql = "UPDATE borrowing_records SET return_date = ? WHERE record_id = ?";
        LocalDate returnDate = LocalDate.now();
        
        try (PreparedStatement statement = connection.prepareStatement(updateSql)) {
            statement.setDate(1, Date.valueOf(returnDate));
            statement.setInt(2, recordId);
            
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                return false;
            }
        }
        
        // Update the book's available copies
        String bookSql = "UPDATE books SET copies_available = copies_available + 1 WHERE book_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(bookSql)) {
            statement.setInt(1, record.getBookId());
            statement.executeUpdate();
        }
        
        return true;
    }
    
    // Read - Get borrowing record by ID
    public BorrowingRecord getBorrowingRecordById(int recordId) throws SQLException {
        String sql = "SELECT * FROM borrowing_records WHERE record_id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, recordId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return extractBorrowingRecordFromResultSet(resultSet);
                }
            }
        }
        
        return null;
    }
    
    // Read - Get active borrowings (not returned yet)
    public List<BorrowingRecord> getActiveBorrowings() throws SQLException {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrowing_records WHERE return_date IS NULL";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                records.add(extractBorrowingRecordFromResultSet(resultSet));
            }
        }
        
        return records;
    }
    
    // Read - Get borrowing history for a patron
    public List<BorrowingRecord> getPatronBorrowingHistory(int patronId) throws SQLException {
        List<BorrowingRecord> records = new ArrayList<>();
        String sql = "SELECT * FROM borrowing_records WHERE patron_id = ? ORDER BY borrow_date DESC";
        
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, patronId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(extractBorrowingRecordFromResultSet(resultSet));
                }
            }
        }
        
        return records;
    }
    
    // Helper method
    private BorrowingRecord extractBorrowingRecordFromResultSet(ResultSet resultSet) throws SQLException {
        int recordId = resultSet.getInt("record_id");
        int bookId = resultSet.getInt("book_id");
        int patronId = resultSet.getInt("patron_id");
        LocalDate borrowDate = resultSet.getDate("borrow_date").toLocalDate();
        LocalDate dueDate = resultSet.getDate("due_date").toLocalDate();
        
        Date returnDateDb = resultSet.getDate("return_date");
        LocalDate returnDate = (returnDateDb != null) ? returnDateDb.toLocalDate() : null;
        
        return new BorrowingRecord(recordId, bookId, patronId, borrowDate, dueDate, returnDate);
    }
}