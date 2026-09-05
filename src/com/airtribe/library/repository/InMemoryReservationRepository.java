package com.airtribe.library.repository;

import com.airtribe.library.entity.Reservation;

import java.util.*;

public class InMemoryReservationRepository implements ReservationRepository {

    private final Map<String, Queue<Reservation>> reservationsByBook = new HashMap<>();
    @Override
    public void save(Reservation reservation) {
        String isbn = reservation.getBook().getIsbn();

        reservationsByBook
                .computeIfAbsent(isbn, key -> new LinkedList<>())
                .offer(reservation);
    }

    @Override
    public List<Reservation> findByBook(String isbn) {
        Queue<Reservation> queue = reservationsByBook.get(isbn);

        if (queue == null) {
            return new ArrayList<>();
        }

        return new ArrayList<>(queue);
    }

    @Override
    public List<Reservation> findByPatron(String patronId) {
        List<Reservation> result = new ArrayList<>();

        for (Queue<Reservation> queue : reservationsByBook.values()) {
            for (Reservation reservation : queue) {
                if (reservation.getPatron()
                        .getPatronId()
                        .equals(patronId)) {

                    result.add(reservation);
                }
            }
        }

        return result;
    }

    @Override
    public void delete(Reservation reservation) {
        String isbn = reservation.getBook().getIsbn();

        Queue<Reservation> queue = reservationsByBook.get(isbn);

        if (queue != null) {
            queue.remove(reservation);

            if (queue.isEmpty()) {
                reservationsByBook.remove(isbn);
            }
        }
    }

    @Override
    public List<Reservation> findAll() {
        List<Reservation> result = new ArrayList<>();

        for (Queue<Reservation> queue : reservationsByBook.values()) {
            result.addAll(queue);
        }

        return result;
    }
    public Reservation pollNextReservation(String isbn) {
        Queue<Reservation> queue = reservationsByBook.get(isbn);

        if (queue == null) {
            return null;
        }

        Reservation reservation = queue.poll();

        if (queue.isEmpty()) {
            reservationsByBook.remove(isbn);
        }

        return reservation;
    }


}
