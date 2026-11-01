package com.epam.assessment.library.service;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Loan;
import com.epam.assessment.library.persistence.BookRepository;
import com.epam.assessment.library.persistence.LoanRepository;
import com.epam.assessment.library.service.response.BookUsage;
import com.epam.assessment.library.service.response.RatedBook;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class BookLoanService {
    private BookRepository bookRepository;
    private LoanRepository loanRepository;

    public BookLoanService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }

    public List<RatedBook> queryTopRatedBooks(int topN) {
        return loanRepository.getLoans().stream()
                .filter(loan -> loan.getRating() != null)
                .collect(Collectors.groupingBy(
                        Loan::getBook,
                        Collectors.averagingDouble(Loan::getRating)
                ))
                .entrySet().stream()
                .map(entry -> {
                    double roundedRating = BigDecimal.valueOf(entry.getValue())
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                    return new RatedBook(entry.getKey(), roundedRating);
                })
                .sorted(Comparator.comparingDouble(RatedBook::rating).reversed())
                .limit(topN)
                .toList();
    }

    public List<BookUsage> queryMostWantedBooks(int topN) {
        return loanRepository.getLoans().stream()
                .collect(Collectors.groupingBy(
                        Loan::getBook,
                        Collectors.counting()
                ))
                .entrySet().stream()
                .map(entry -> new BookUsage(entry.getKey(), entry.getValue().intValue()))
                .sorted(Comparator.comparingInt(BookUsage::count).reversed())
                .limit(topN)
                .toList();
    }

    public Loan createLoan(long bookId) {
        Loan loan = new Loan(
                loanRepository.getNextLoanId(),
                bookRepository.findById(bookId)
                        .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId)),
                LocalDateTime.now(),
                null,
                null
        );
        loanRepository.saveLoan(loan);
        return loan;
    }

    public void returnBook(long loanId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5 inclusive");
        }

        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found with ID: " + loanId));

        if (loan.getReturnDate() != null) {
            throw new IllegalStateException("Loan with ID: " + loanId + " is already returned");
        }

        Loan newLoan = new Loan(
                loanId,
                loan.getBook(),
                loan.getBorrowDate(),
                LocalDateTime.now(),
                rating
        );

        loanRepository.removeLoan(loan);
        loanRepository.saveLoan(newLoan);
    }
}