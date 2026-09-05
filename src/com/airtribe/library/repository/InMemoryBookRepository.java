package com.airtribe.library.repository;

import com.airtribe.library.entity.Book;

import java.util.*;
import java.util.stream.Collectors;

public class InMemoryBookRepository implements BookRepository {

    private final Map<String, Book> books = new HashMap<>();
    @Override
    public void save(Book book) {
        books.put(book.getIsbn(), book);
    }

    @Override
    public void delete(String Isbn) {
        books.remove(Isbn);
    }

    @Override
    public Optional<Book> findByIsbn(String Isbn) {
        return Optional.ofNullable(books.get(Isbn));
    }

    @Override
    public List<Book> findByTitle(String title) {
        return books.values().stream()
                .filter(book -> book.getTitle().equalsIgnoreCase(title)).collect(Collectors.toList());
    }

    @Override
    public List<Book> findByAuthor(String author) {
        return books.values().stream()
                .filter(book -> book.getAuthor().equalsIgnoreCase(author)).collect(Collectors.toList());
    }

    @Override
    public List<Book> findAll() {
        return new ArrayList<>(books.values());
    }

    @Override
    public boolean existsByIsbn(String Isbn) {
        return books.containsKey(Isbn);
    }
}
