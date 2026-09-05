package com.airtribe.library.entity;

import java.util.HashSet;
import java.util.Set;

public class LibraryBranch {

    private final String branchId;
    private String branchName;
    private String location;

    private final Set<Book> books;

    public LibraryBranch(String branchId, String branchName, String location) {
        this.branchId = branchId;
        this.branchName = branchName;
        this.location = location;
        this.books = new HashSet<>();
    }

    public String getBranchId() {
        return branchId;
    }

    public String getbranchName() {
        return branchName;
    }

    public String getLocation() {
        return location;
    }

    public Set<Book> getBooks() {
        return new HashSet<>(books);
    }

    public void addBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("Book cannot be null.");
        }

        books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public boolean hasBook(String isbn) {
        return books.stream().anyMatch(book -> book.getIsbn().equals(isbn));
    }
}