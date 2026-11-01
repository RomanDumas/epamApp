package com.epam.assessment.library.persistence;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Loan;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoanRepository {
    private String filePath;
    private BookRepository bookRepository;
    public static String CSV_SEPARATOR = ",";
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Loan> readLoanCsv(){
        List<Loan> loanList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] values = line.split(",");
                long loanId = Long.parseLong(values[0].trim());

                long bookId = Long.parseLong(values[1].trim());
                Optional<Book> opBook = bookRepository.findById(bookId);

                if(opBook.isEmpty()){
                    throw new IllegalArgumentException("book not found with id: " + bookId);
                }

                Book book = opBook.get();

                LocalDateTime borrowDate = null;
                try {
                    borrowDate = LocalDateTime.parse(values[2].trim());
                } catch (DateTimeParseException e) {
                    System.out.println("error of date parse (borrowDate): " + e);
                }

                LocalDateTime returnDate = null;
                if (values.length > 3 && !values[3].trim().isEmpty()) {
                    try {
                        returnDate = LocalDateTime.parse(values[3].trim());
                    } catch (DateTimeParseException e) {
                        System.out.println("error of date parse (returnDate): " + e);
                    }
                }

                Integer rating = null;
                if(values.length > 4 && !values[4].trim().isEmpty()){
                    rating = Integer.parseInt(values[4].trim());
                }

                loanList.add(new Loan(loanId, book, borrowDate, returnDate, rating));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loanList;
    }
    public void loadLoans() {
        this.loanList = readLoanCsv();
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
                .orElse(0) + 1;
    }

    public void saveLoan(Loan loan){
        loanList.add(loan);
    }
    public void removeLoan(Loan loan){
        loanList.remove(loan);
    }
}
