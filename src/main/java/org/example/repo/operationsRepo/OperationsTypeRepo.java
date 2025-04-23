package org.example.repo.operationsRepo;

import org.example.entity.operationsType.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OperationsTypeRepo extends JpaRepository<OperationType, Integer> {

    boolean existsByRefKeyAndNameAndTime(String refKey, String nomenclatureName, double time);

    @Query("SELECT ot FROM OperationType ot WHERE ot.inTimeLine = false AND ot.markForDelete = false")
    List<OperationType> findByNotInTimeLine();



}
