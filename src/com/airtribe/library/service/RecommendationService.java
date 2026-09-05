package com.airtribe.library.service;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Patron;
import com.airtribe.library.enums.BookStatus;
import com.airtribe.library.strategy.RecommendationStrategy;

import java.util.List;
import java.util.stream.Collectors;

public class RecommendationService {

    private final BookService bookService;
    private RecommendationStrategy strategy;

    public RecommendationService(BookService bookService, RecommendationStrategy strategy) {

        this.bookService = bookService;
        this.strategy = strategy;
    }

    public void setStrategy(RecommendationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Recommendation strategy cannot be null."
            );
        }

        this.strategy = strategy;
    }

    public List<Book> getRecommendations(Patron patron) {

        if (patron == null) {
            throw new IllegalArgumentException(
                    "Patron cannot be null."
            );
        }

        List<Book> availableBooks = bookService.getAllBooks()
                .stream()
                .filter(book -> book.getStatus() == BookStatus.AVAILABLE)
                .collect(Collectors.toList());

        return strategy.recommend(patron, availableBooks);
    }
}