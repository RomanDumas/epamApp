package com.epam.assessment.library.persistence;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Genre;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {
    private String filePath;
    private List<Book> bookList = new ArrayList<>();
    public static String CSV_SEPARATOR = ",";

    public BookRepository(String filePath){
        this.filePath = filePath;
    }

    public void loadBooks() {
        bookList = readBookCsv();
    }

    public Optional<Book> findById(long bookId) {
        return bookList.stream()
                .filter(book -> book.getBookId() == bookId)
                .findFirst();
    }

    private List<Book> readBookCsv(){
        List<Book> bookList = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }

                String[] values = line.split(CSV_SEPARATOR);
                long bookId = Long.parseLong(values[0].trim());
                String ISBN = values[1].trim();
                String author = values[2].trim();
                String title = values[3].trim();
                Genre genre = Genre.valueOf(values[4].trim().toUpperCase());

                bookList.add(new Book(bookId, ISBN, author, title, genre));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookList;
    }

    public List<Book> getBooks(){
        return bookList;
    }

}
