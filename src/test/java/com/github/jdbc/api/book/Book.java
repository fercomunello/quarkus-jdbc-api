package com.github.jdbc.api.book;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public final class Book {

    private final UUID uuid;
    private String title;
    private final String author;
    private final String genre;
    private final String publisher;
    private final short publishYear;
    private final boolean inStock;
    private final LocalDateTime createdAt;

    public Book(String title, String author, String genre,
                String publisher, short publishYear,
                boolean inStock, LocalDateTime createdAt) {
        this(null, title, author, genre, publisher, publishYear, inStock, createdAt);
    }

    public Book(UUID uuid, String title, String author,
                String genre, String publisher, short publishYear,
                boolean inStock, LocalDateTime createdAt) {
        this.uuid = uuid;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.publisher = publisher;
        this.publishYear = publishYear;
        this.inStock = inStock;
        this.createdAt = createdAt;
    }

    public void changeTitle(final String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Book) obj;
        return this.uuid != null
                ? Objects.equals(this.uuid, that.uuid)
                : Objects.equals(this.title, that.title);
    }

    @Override
    public int hashCode() {
        return this.uuid != null
                ? Objects.hash(this.uuid)
                : Objects.hash(this.title);
    }

    @Override
    public String toString() {
        return "Book[" +
               "uuid=" + uuid + ", " +
               "title=" + title + ", " +
               "author=" + author + ", " +
               "genre=" + genre + ", " +
               "publisher=" + publisher + ", " +
               "publishYear=" + publishYear + ", " +
               "inStock=" + inStock + ", " +
               "createdAt=" + createdAt + ']';
    }

    public UUID uuid() {
        return uuid;
    }

    public String title() {
        return title;
    }

    public String author() {
        return author;
    }

    public String genre() {
        return genre;
    }

    public String publisher() {
        return publisher;
    }

    public short publishYear() {
        return publishYear;
    }

    public boolean inStock() {
        return inStock;
    }

    public LocalDateTime createdAt() {
        return createdAt;
    }

}
