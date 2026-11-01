package com.epam.assessment.library;

import com.epam.assessment.library.persistence.BookRepository;
import com.epam.assessment.library.persistence.LoanRepository;
import com.epam.assessment.library.service.BookLoanService;
import com.epam.assessment.library.presentation.UserInterface;

/*
=======WHAT I NEED TO DO AFTER DOTA 2=========
    -change all local variables to final
    -write userInterface class
    -check all exceptions
    -recheck all demands to assessment
 */

public class Application {
    private static final String BOOKS_CSV_PATH = "test_data/books.csv";
    private static final String LOANS_CSV_PATH = "test_data/loans.csv";

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final BookLoanService bookLoanService;
    private final UserInterface userInterface;

    public static void main(String[] args) {
        Application application = new Application();
        application.start();
    }

    public Application() {
        this.bookRepository = new BookRepository(BOOKS_CSV_PATH);
        this.loanRepository = new LoanRepository(LOANS_CSV_PATH, bookRepository);
        this.bookLoanService = new BookLoanService(bookRepository, loanRepository);
        this.userInterface = new UserInterface(bookLoanService);
    }

    private void start() {
        bookRepository.loadBooks();
        loanRepository.loadLoans();
        userInterface.interact();

        loanRepository.updateCsv();
    }
}
