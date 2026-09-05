package com.airtribe.library.strategy;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.Loan;
import com.airtribe.library.entity.Patron;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.stream.Collectors;

public class AuthorRecommendationStrategy implements RecommendationStrategy {

        @Override
        public List<Book> recommend(Patron patron,List<Book> availableBooks) {

            Set<String> borrowedIsbns = patron.getBorrowingHistory()
                    .stream()
                    .map(Loan::getBook)
                    .map(Book::getIsbn)
                    .collect(Collectors.toSet());

            Set<String> borrowedAuthors = patron.getBorrowingHistory()
                    .stream()
                    .map(Loan::getBook)
                    .map(Book::getAuthor)
                    .collect(Collectors.toSet());

            return availableBooks.stream()
                    .filter(book -> borrowedAuthors.contains(book.getAuthor()))
                    .filter(book -> !borrowedIsbns.contains(book.getIsbn()))
                    .collect(Collectors.toList());
        }
}