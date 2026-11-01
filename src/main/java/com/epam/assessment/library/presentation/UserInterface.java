package com.epam.assessment.library.presentation;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Loan;
import com.epam.assessment.library.service.BookLoanService;
import com.epam.assessment.library.service.response.BookUsage;
import com.epam.assessment.library.service.response.RatedBook;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class UserInterface {
    private BookLoanService bookLoanService;
    private Scanner scanner = new Scanner(System.in);

    public UserInterface(BookLoanService bookLoanService) {
        this.bookLoanService = bookLoanService;
    }

    public void interact() {
        boolean inLoop = true;
        while (inLoop) {
            menu();
            String inputStr = scanner.nextLine().trim();
            if (inputStr.isEmpty()) {
                continue;
            }
            try {
                int input = Integer.parseInt(inputStr);
                switch (input) {
                    case 0 -> {
                        System.out.println("Exiting application. Goodbye!");
                        inLoop = false;
                    }
                    case 1 -> printTopRatedBooks();
                    case 2 -> printMostWantedBooks();
                    case 3 -> enterLoan();
                    case 4 -> returnBook();
                    default -> System.out.println("Invalid number. Please try again");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Please try again");
            }
        }
    }

    private void menu() {
        System.out.print("Library Manager Menu:\n" +
                "1. Query top rated books\n" +
                "2. Query most wanted books\n" +
                "3. Enter loan\n" +
                "4. Return book\n" +
                "0. Exit\n" +
                "Select a menu option: ");
    }

    private void printTopRatedBooks() {
        List<RatedBook> ratedBookList = bookLoanService.queryTopRatedBooks(3);
        System.out.println("Top 3 rated books:");
        for (RatedBook ratedBook : ratedBookList) {
            Book book = ratedBook.book();
            System.out.println(book.getTitle() + " (" + book.getAuthor() + "), Avg Rating: " + String.format(Locale.US, "%.2f", ratedBook.rating()));
        }
    }

    private void printMostWantedBooks() {
        List<BookUsage> bookUsageList = bookLoanService.queryMostWantedBooks(3);
        System.out.println("Top 3 most wanted books:");
        for (BookUsage bookUsage : bookUsageList) {
            Book book = bookUsage.book();
            System.out.println(book.getTitle() + " (" + book.getAuthor() + "), Borrowed: " + bookUsage.count() + " times");
        }
    }

    private void enterLoan() {
        long bookId = getValidLongInput("Enter book ID: ");
        try {
            Loan newLoan = bookLoanService.createLoan(bookId);
            System.out.println("Loan created successfully.");
            System.out.println("Loan ID: " + newLoan.getLoanId() + ", Book: " + newLoan.getBook().getTitle() + " (" + newLoan.getBook().getAuthor() + "), Borrow Date: " + newLoan.getBorrowDate());
        } catch (Exception e) {
            System.out.println("Failed to enter loan: " + e.getMessage());
        }
    }

    private void returnBook() {
        long loanId = getValidLongInput("Enter loan ID to return: ");
        int rating = getValidIntInput("Enter rating (1-5): ");
        try {
            bookLoanService.returnBook(loanId, rating);
            System.out.println("Book returned successfully.");
        } catch (Exception e) {
            System.out.println("Failed to return book: " + e.getMessage());
        }
    }

    private long getValidLongInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Long.parseLong(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
            }
        }
    }

    private int getValidIntInput(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number");
            }
        }
    }
}