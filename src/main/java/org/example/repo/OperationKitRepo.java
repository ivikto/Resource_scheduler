package org.example.repo;

import org.example.entity.operations_type.OperationKit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OperationKitRepo extends JpaRepository<OperationKit, Integer> {

    boolean existsByRefKeyAndNameAndTime(String refKey, String nomenclatureName, double time);

    @Query("SELECT ot FROM OperationKit ot" +
            " WHERE ot.inTimeLine = false" +
            " AND ot.markForDelete = false" +
            " AND ot.isFinish = false")
    List<OperationKit> findByNotInTimeLine();

    @Query("SELECT ot FROM OperationKit ot WHERE ot.refKey = :ref_key")
    List<OperationKit> findByRefKey(String refKey);



}
