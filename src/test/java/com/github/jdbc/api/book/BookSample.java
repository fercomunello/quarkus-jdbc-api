package com.github.jdbc.api.book;

import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;

@ApplicationScoped
public final class BookSample {

    public Book mangaBook() {
        return new Book(
                "Naruto, Vol. 1: Uzumaki Naruto", "Masashi Kishimoto", "Shonen",
                "VIZ Media LLC", (short) 2003, true, LocalDateTime.now()
        );
    }

    public Book anotherMangaBook() {
        return new Book(
                "Naruto, Vol. 1: Uzumaki Naruto", "Masashi Kishimoto", "Shonen",
                "VIZ Media LLC", (short) 2003, true, LocalDateTime.now()
        );
    }

}
