package com.airtribe.library.service;

import com.airtribe.library.entity.Loan;
import com.airtribe.library.entity.Patron;
import com.airtribe.library.exception.PatronNotFoundException;
import com.airtribe.library.repository.PatronRepository;
import com.airtribe.library.util.LoggerUtil;

import java.util.List;
import java.util.logging.Logger;

public class PatronService {

    private static final Logger logger =
            LoggerUtil.getLogger(PatronService.class);

    private final PatronRepository patronRepository;

    public PatronService(PatronRepository patronRepository) {
        this.patronRepository = patronRepository;
    }

    public void addPatron(Patron patron) {

        if (patron == null) {
            logger.warning("Attempted to add a null patron.");
            throw new IllegalArgumentException("Patron cannot be null.");
        }

        if (patronRepository.existsById(patron.getPatronId())) {
            logger.warning(
                    "Attempted to add duplicate patron: "
                            + patron.getPatronId()
            );

            throw new IllegalArgumentException(
                    "Patron with ID " + patron.getPatronId()
                            + " already exists."
            );
        }

        patronRepository.save(patron);

        logger.info(
                "Patron added successfully: "
                        + patron.getPatronId()
        );
    }

    public void removePatron(String patronId) {

        if (!patronRepository.existsById(patronId)) {
            logger.warning(
                    "Attempted to remove non-existent patron: "
                            + patronId
            );

            throw new PatronNotFoundException(
                    "Patron with ID " + patronId + " not found."
            );
        }

        patronRepository.delete(patronId);

        logger.info(
                "Patron removed successfully: " + patronId
        );
    }

    public Patron getPatronById(String patronId) {

        return patronRepository.findById(patronId)
                .orElseThrow(() -> {

                    logger.warning(
                            "Patron not found: " + patronId
                    );

                    return new PatronNotFoundException(
                            "Patron with ID " + patronId
                                    + " not found."
                    );
                });
    }

    public void updatePatron(Patron patron) {

        if (patron == null) {
            logger.warning("Attempted to update a null patron.");
            throw new IllegalArgumentException(
                    "Patron cannot be null."
            );
        }

        if (!patronRepository.existsById(patron.getPatronId())) {
            logger.warning(
                    "Attempted to update non-existent patron: "
                            + patron.getPatronId()
            );

            throw new PatronNotFoundException(
                    "Patron with ID " + patron.getPatronId()
                            + " not found."
            );
        }

        patronRepository.save(patron);

        logger.info(
                "Patron updated successfully: "
                        + patron.getPatronId()
        );
    }

    public List<Patron> searchByName(String name) {

        logger.info(
                "Searching patrons by name: " + name
        );

        return patronRepository.findByName(name);
    }

    public List<Patron> getAllPatrons() {

        logger.info("Fetching all patrons.");

        return patronRepository.findAll();
    }

    public List<Loan> getBorrowingHistory(String patronId) {

        logger.info(
                "Fetching borrowing history for patron: "
                        + patronId
        );

        Patron patron = getPatronById(patronId);

        return patron.getBorrowingHistory();
    }
}