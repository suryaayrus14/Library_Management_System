package com.airtribe.library.repository;

import com.airtribe.library.entity.LibraryBranch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class InMemoryBranchRepository implements BranchRepository {

    private final Map<String, LibraryBranch> branches = new HashMap<>();

    @Override
    public void save(LibraryBranch branch) {
        branches.put(branch.getBranchId(), branch);
    }

    @Override
    public void delete(String branchId) {
        branches.remove(branchId);
    }

    @Override
    public Optional<LibraryBranch> findById(String branchId) {
        return Optional.ofNullable(branches.get(branchId));
    }

    @Override
    public List<LibraryBranch> findAll() {
        return new ArrayList<>(branches.values());
    }

    @Override
    public boolean existsById(String branchId) {
        return branches.containsKey(branchId);
    }
}