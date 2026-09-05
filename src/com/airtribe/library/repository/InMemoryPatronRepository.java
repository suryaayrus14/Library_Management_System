package com.airtribe.library.repository;

import com.airtribe.library.entity.Patron;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryPatronRepository implements PatronRepository {
    private final Map<String, Patron> patrons = new HashMap<>();

    @Override
    public void save(Patron patron)  {
        patrons.put(patron.getPatronId(), patron);
    }

    @Override
    public void delete(String patronId)  {
        patrons.remove(patronId);
    }

    public Optional<Patron> findById(String patronId) {
        return Optional.ofNullable(patrons.get(patronId));
    }

    public List<Patron> findByName(String name) {
        return patrons.values().stream()
                .filter(patron -> patron.getName().equalsIgnoreCase(name))
                .toList();
    }

    public List<Patron> findAll() {
        return patrons.values().stream().toList();
    }

    public boolean existsById(String patronId) {
        return patrons.containsKey(patronId);
    }


}
