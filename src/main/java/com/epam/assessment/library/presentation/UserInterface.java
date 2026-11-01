package com.epam.assessment.library.presentation;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Loan;
import com.epam.assessment.library.service.BookLoanService;
import com.epam.assessment.library.service.response.BookUsage;
import com.epam.assessment.library.service.response.RatedBook;

import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private BookLoanService bookLoanService;

    public UserInterface(BookLoanService bookLoanService) {
        this.bookLoanService = bookLoanService;
    }

    public void interact() {
        int input;
        boolean inLoop = true;
        while(inLoop){
            menu();
            try(Scanner scanner = new Scanner(System.in)){
                switch(input = scanner.nextInt()){
                    case 0 ->  inLoop = false;
                    case 1 ->  printTopRatedBooks();
                    case 2 ->  printMostWantedBooks();
                    case 3 ->  enterLoan();
                    case 4 ->  returnBook();
                    default -> {
                        System.out.println("Invalid number. Please try again ");
                    }
                }

            }
        }


    }
    private void menu(){
        System.out.println("Library Manager Menu:\n" +
                "1. Query top rated books\n" +
                "2. Query most wanted books\n" +
                "3. Enter loan\n" +
                "4. Return book\n" +
                "0. Exit\n" +
                "Select a menu option: ");
    }
    private void printTopRatedBooks(){
        List<RatedBook> ratedBookList = bookLoanService.queryTopRatedBooks(3);
        System.out.println("Top " + 3 + "rated books:\n");
        for(RatedBook ratedBook : ratedBookList){
            Book book = ratedBook.book();
            System.out.println(book.getTitle() + "(" + book.getAuthor() + ")" + ", Avg Rating: " + ratedBook.rating());
        }
    }
    private void printMostWantedBooks(){
        List<BookUsage> bookUsageList = bookLoanService.queryMostWantedBooks(3);
        System.out.println("Top " + 3 + "most wanted books:\n");
        for(BookUsage bookUsage : bookUsageList){
            Book book = bookUsage.book();
            System.out.println(book.getTitle() + "(" + book.getAuthor() + ")" + ", Borrowed: " + bookUsage.count());
        }
    }
    private void enterLoan(){
        try(Scanner scanner = new Scanner(System.in)){
            System.out.println("enter book ID: ");
            long bookId = scanner.nextLong();
            Loan newLoan = bookLoanService.createLoan(bookId);
            System.out.println("loan created successfully");
            System.out.println(newLoan);
        }
    }

    private void returnBook(){
        try(Scanner scanner = new Scanner(System.in)){
            System.out.println("what loan do you want to return: ");
            long loanId = scanner.nextLong();

            System.out.println("enter rating (1-5):");
            int rating = scanner.nextInt();
            //перевірка щоб була від 1 до 5

            bookLoanService.returnBook(loanId, rating);
        }

    }


}
