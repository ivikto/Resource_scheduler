package org.example.repo;

import org.example.entity.Production;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductionRepo extends JpaRepository<Production, String> {

    boolean existsByRefKey(String refKey);

    @EntityGraph(attributePaths = "operations")
    @Query("SELECT p FROM Production p")
    List<Production> findAllWithOperations();
}
