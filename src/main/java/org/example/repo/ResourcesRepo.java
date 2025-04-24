package org.example.repo;

import org.example.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourcesRepo extends JpaRepository<Resources, Integer> {

    public Optional<Resources> findFirstByName(String name);
}
