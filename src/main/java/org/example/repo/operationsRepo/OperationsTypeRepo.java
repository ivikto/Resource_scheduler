package org.example.repo.operationsRepo;

import org.example.entity.operations_type.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OperationsTypeRepo extends JpaRepository<OperationType, Integer> {

    boolean existsByRefKeyAndNameAndTime(String refKey, String nomenclatureName, double time);

    @Query("SELECT ot FROM OperationType ot" +
            " WHERE ot.inTimeLine = false" +
            " AND ot.markForDelete = false" +
            " AND ot.isFinish = false")
    List<OperationType> findByNotInTimeLine();

    @Query("SELECT ot FROM OperationType ot WHERE ot.refKey = :ref_key")
    List<OperationType> findByRefKey(String ref_key);



}
