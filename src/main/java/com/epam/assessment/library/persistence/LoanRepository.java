package com.epam.assessment.library.persistence;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Loan;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanRepository {
    private String filePath;
    private BookRepository bookRepository;
    public static final String CSV_SEPARATOR = ",";
    List<Loan> loanList = new ArrayList<>();

    public LoanRepository(String filePath, BookRepository bookRepository) {
        this.filePath = filePath;
        this.bookRepository = bookRepository;
    }

    public void updateCsv() {
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"))) {
            bw.write("loanId,bookId,borrowDate,returnDate,rating");
            bw.newLine();

            for (Loan loan : loanList) {
                StringBuilder oneLine = new StringBuilder();
                oneLine.append(loan.getLoanId()).append(CSV_SEPARATOR);

                if (loan.getBook() != null) {
                    oneLine.append(loan.getBook().getBookId());
                }
                oneLine.append(CSV_SEPARATOR);
                oneLine.append(loan.getBorrowDate());

                if (loan.getReturnDate() != null) {
                    oneLine.append(CSV_SEPARATOR).append(loan.getReturnDate());
                    if (loan.getRating() != null) {
                        oneLine.append(CSV_SEPARATOR).append(loan.getRating());
                    }
                }

                bw.write(oneLine.toString());
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error writing to loans CSV file at path: " + filePath, e);
        }
    }

    public void loadLoans() {
        loanList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }

                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                try {
                    String[] values = line.split(CSV_SEPARATOR);
                    if (values.length < 3) {
                        throw new IllegalArgumentException("Invalid CSV row format: missing columns.");
                    }

                    long loanId = Long.parseLong(values[0].trim());
                    long bookId = Long.parseLong(values[1].trim());

                    Book book = bookRepository.findById(bookId)
                            .orElseThrow(() -> new IllegalArgumentException("Book not found with ID: " + bookId));

                    LocalDateTime borrowDate = LocalDateTime.parse(values[2].trim());
                    LocalDateTime returnDate = null;
                    Integer rating = null;

                    if (values.length > 3 && !values[3].trim().isEmpty()) {
                        returnDate = LocalDateTime.parse(values[3].trim());
                    }

                    if (values.length > 4 && !values[4].trim().isEmpty()) {
                        rating = Integer.parseInt(values[4].trim());
                    }

                    loanList.add(new Loan(loanId, book, borrowDate, returnDate, rating));
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException | DateTimeParseException e) {
                    throw new IllegalArgumentException("Invalid CSV row format in line: " + line, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading loans CSV file at path: " + filePath, e);
        }
    }

    public List<Loan> getLoans() {
        return loanList;
    }

    public Optional<Loan> findById(long loanId) {
        return loanList.stream()
                .filter(loan -> loan.getLoanId() == loanId)
                .findFirst();
    }

    public long getNextLoanId() {
        return loanList.stream()
                .mapToLong(Loan::getLoanId)
                .max()
                .orElse(1000) + 1;
    }

    public void saveLoan(Loan loan) {
        loanList.add(loan);
    }

    public void removeLoan(Loan loan) {
        loanList.remove(loan);
    }
}