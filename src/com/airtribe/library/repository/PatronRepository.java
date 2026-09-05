package com.airtribe.library.repository;

import com.airtribe.library.entity.Patron;

import java.util.List;
import java.util.Optional;

public interface PatronRepository {

    void save(Patron patron);

    void delete(String patronId);

    Optional<Patron> findById(String patronId);

    List<Patron> findByName(String name);

    List<Patron> findAll();

    boolean existsById(String patronId);
}