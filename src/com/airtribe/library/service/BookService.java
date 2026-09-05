package com.airtribe.library.service;

import com.airtribe.library.entity.Book;
import com.airtribe.library.exception.BookNotFoundException;
import com.airtribe.library.repository.BookRepository;
import com.airtribe.library.util.LoggerUtil;

import java.util.logging.Logger;


import java.util.List;

public class BookService {

    private static final Logger logger =
            LoggerUtil.getLogger(BookService.class);


    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public void addBook(Book book) {

        if(book == null) {
            logger.warning("Attempted to add a null book.");
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if(bookRepository.existsByIsbn(book.getIsbn())) {
            logger.warning(
                    "Attempted to add duplicate book: "
                            + book.getIsbn()
            );
            throw new IllegalArgumentException("Book with ISBN " + book.getIsbn() + " already exists.");
        }
        bookRepository.save(book);
        logger.info(
                "Book added successfully: " + book.getIsbn()
        );
    }

    public void removeBook(String isbn) {
        if (!bookRepository.existsByIsbn(isbn)) {
            logger.warning(
                    "Attempted to remove non-existent book: " + isbn
            );
            throw new BookNotFoundException(
                    "Book with ISBN " + isbn + " not found"
            );
        }

        bookRepository.delete(isbn);
        logger.info(
                "Book removed successfully: " + isbn
        );
    }

    public void updateBook(Book book) {
        if(book == null) {
            logger.warning("Attempted to update a null book.");
            throw new IllegalArgumentException("Book cannot be null.");
        }
        if(!bookRepository.existsByIsbn(book.getIsbn())) {
            logger.warning("Attempted to update non-existent book: " + book.getIsbn());
            throw new BookNotFoundException("Book with ISBN " + book.getIsbn() + " does not exist.");
        }
        bookRepository.save(book);
        logger.info("Book updated successfully: " + book.getIsbn());
    }

    public Book getBookByIsbn(String isbn) {

        return bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> {
                    logger.warning("Book not found: " + isbn);

                    return new BookNotFoundException("Book with ISBN " + isbn + " not found.");
                });
    }

    public List<Book> searchByTitle(String title) {
        logger.info("Searching books by title: " + title);

        return bookRepository.findByTitle(title);
    }

    public List<Book> searchByAuthor(String author) {
        logger.info("Searching books by author: " + author);

        return bookRepository.findByAuthor(author);
    }

    public List<Book> getAllBooks() {
        logger.info("Fetching All books");
        return bookRepository.findAll();
    }


}
