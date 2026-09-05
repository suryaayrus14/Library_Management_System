package com.airtribe.library.strategy;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Patron;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RecentPublicationRecommendationStrategy implements RecommendationStrategy {

    @Override
    public List<Book> recommend(Patron patron, List<Book> availableBooks) {
        return availableBooks.stream()
                .sorted(Comparator.comparing(
                        Book::getPublicationYear).reversed())
                .collect(Collectors.toList());
    }
}
