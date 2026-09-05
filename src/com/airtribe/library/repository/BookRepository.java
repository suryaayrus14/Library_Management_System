package com.airtribe.library.repository;

import com.airtribe.library.entity.Book;

import java.util.List;
import java.util.Optional;

public interface BookRepository {
    void save(Book book);

    void delete(String Isbn);

    Optional<Book> findByIsbn(String Isbn);
    List<Book> findByTitle(String title);

    List<Book> findByAuthor(String author);

    List<Book> findAll();

    boolean existsByIsbn(String Isbn);

}

