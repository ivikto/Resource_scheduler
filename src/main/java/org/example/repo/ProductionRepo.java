package org.example.repo;

import org.example.entity.Production;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductionRepo extends JpaRepository<Production, String> {

    boolean existsByRefKey(String refKey);


    @Query("SELECT p FROM Production p LEFT JOIN FETCH p.operations")
    List<Production> findAllWithOperations();
}
