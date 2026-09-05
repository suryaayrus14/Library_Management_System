package com.airtribe.library.strategy;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Patron;

import java.util.List;

public interface RecommendationStrategy {
        List<Book> recommend(Patron patron, List<Book> availableBooks);
}