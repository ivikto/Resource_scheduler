package org.example.repo;

import org.example.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StatusRepo extends JpaRepository<Status, Integer> {

    @Query("SELECT s.refKey FROM Status s WHERE s.name = :name")
    String findRefKeyByName(@Param("name") String name);
}
