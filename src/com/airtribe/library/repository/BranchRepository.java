package com.airtribe.library.repository;

import com.airtribe.library.entity.LibraryBranch;

import java.util.List;
import java.util.Optional;

public interface BranchRepository {

    void save(LibraryBranch branch);

    void delete(String branchId);

    Optional<LibraryBranch> findById(String branchId);

    List<LibraryBranch> findAll();

    boolean existsById(String branchId);
}