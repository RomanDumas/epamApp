package com.epam.assessment.library.domain;

public class Book {
    private long bookId;
    private String ISBN;
    private String author;
    private String title;
    private Genre genre;

    public Book(long bookId, String ISBN, String author, String title, Genre genre) {
        this.bookId = bookId;
        this.ISBN = ISBN;
        this.author = author;
        this.title = title;
        this.genre = genre;
    }

    public long getBookId() { return bookId; }
    public String getISBN() { return ISBN; }
    public String getAuthor() { return author; }
    public String getTitle() { return title; }
    public Genre getGenre() { return genre; }

    @Override
    public String toString() {
        return bookId + " " + title + " (" + author + ") " + genre + " " + ISBN;
    }
}

