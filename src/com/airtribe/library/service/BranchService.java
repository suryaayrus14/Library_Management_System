package com.airtribe.library.service;

import com.airtribe.library.entity.Book;
import com.airtribe.library.entity.LibraryBranch;
import com.airtribe.library.repository.BranchRepository;
import com.airtribe.library.util.LoggerUtil;

import java.util.List;
import java.util.logging.Logger;

public class BranchService {

    private final static Logger logger = LoggerUtil.getLogger(BranchService.class);

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public void addBranch(LibraryBranch branch) {
        if (branch == null) {
            logger.warning("Attempted to add a null branch.");
            throw new IllegalArgumentException("Branch cannot be null.");
        }

        if (branchRepository.existsById(branch.getBranchId())) {
            logger.warning("Attempted to add duplicate branch: " + branch.getBranchId());

            throw new IllegalArgumentException(
                    "Branch with ID " + branch.getBranchId()
                            + " already exists."
            );
        }

        branchRepository.save(branch);

        logger.info("Branch added successfully: " + branch.getBranchId());
    }

    public void removeBranch(String branchId) {
        if (!branchRepository.existsById(branchId)) {
            logger.warning("Attempted to remove non-existent branch: " + branchId);

            throw new IllegalArgumentException("Branch with ID " + branchId + " not found.");
        }

        branchRepository.delete(branchId);

        logger.info("Branch removed successfully: " + branchId);
    }

    public LibraryBranch getBranchById(String branchId) {
        return branchRepository.findById(branchId)
                .orElseThrow(() -> {
                    logger.warning("Attempted to retrieve non-existent branch: " + branchId);
                    return new IllegalArgumentException("Branch with ID " + branchId + " not found.");
                });
    }

    public List<LibraryBranch> getAllBranches() {
        logger.info("Listing all branches");
        return branchRepository.findAll();
    }

    public void addBookToBranch(String branchId, Book book) {
        LibraryBranch branch = getBranchById(branchId);
        branch.addBook(book);
        branchRepository.save(branch);
        logger.info("Book with ISBN " + book.getIsbn() + " added to branch " + branchId);
    }

    public void transferBook(String isbn, String fromBranchId, String toBranchId) {
        LibraryBranch fromBranch = getBranchById(fromBranchId);
        LibraryBranch toBranch = getBranchById(toBranchId);

        if (!fromBranch.hasBook(isbn)) {
            logger.warning("Attempted to transfer non-existent book with ISBN " + isbn + " from branch " + fromBranchId);
            throw new IllegalArgumentException("Book with ISBN " + isbn + " not found in branch " + fromBranchId);
        }

        Book bookToTransfer = fromBranch.getBooks().stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Book with ISBN " + isbn + " not found in branch " + fromBranchId));

        fromBranch.removeBook(bookToTransfer);
        toBranch.addBook(bookToTransfer);

        branchRepository.save(fromBranch);
        branchRepository.save(toBranch);

        logger.info("Book with ISBN " + isbn + " transferred from branch " + fromBranchId + " to branch " + toBranchId);
    }
}
