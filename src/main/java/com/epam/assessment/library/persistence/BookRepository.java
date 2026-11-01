package com.epam.assessment.library.persistence;

import com.epam.assessment.library.domain.Book;
import com.epam.assessment.library.domain.Genre;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookRepository {
    private String filePath;
    private List<Book> bookList = new ArrayList<>();
    public static final String CSV_SEPARATOR = ",";

    public BookRepository(String filePath){
        this.filePath = filePath;
    }

    public void loadBooks() {
        bookList = new ArrayList<>();
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
                    if (values.length < 5) {
                        throw new IllegalArgumentException("Invalid CSV row format: missing columns.");
                    }

                    long bookId = Long.parseLong(values[0].trim());
                    String ISBN = values[1].trim();
                    String author = values[2].trim();
                    String title = values[3].trim();
                    Genre genre = Genre.valueOf(values[4].trim().toUpperCase());

                    bookList.add(new Book(bookId, ISBN, author, title, genre));
                } catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
                    throw new IllegalArgumentException("Invalid CSV row format in line: " + line, e);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading books CSV file at path: " + filePath, e);
        }
    }

    public Optional<Book> findById(long bookId) {
        return bookList.stream()
                .filter(book -> book.getBookId() == bookId)
                .findFirst();
    }

    public List<Book> getBooks(){
        return bookList;
    }
}