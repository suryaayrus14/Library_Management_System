package com.airtribe.library.repository;

import com.airtribe.library.entity.Reservation;

import java.util.List;

public interface ReservationRepository {

    void save(Reservation reservation);

    List<Reservation> findByBook(String isbn);

    List<Reservation> findByPatron(String patronId);

    void delete(Reservation reservation);

    List<Reservation> findAll();

    Reservation pollNextReservation(String isbn);
}