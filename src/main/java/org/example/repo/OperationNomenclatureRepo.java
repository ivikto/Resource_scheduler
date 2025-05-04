package org.example.repo;

import org.example.entity.OperationNomenclature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface OperationNomenclatureRepo extends JpaRepository<OperationNomenclature, Integer> {

    @Query("SELECT s.name FROM OperationNomenclature s WHERE s.refKey = :ref_key")
    String findRefKeyByName(@Param("ref_key") String refKey);

    @Query("SELECT n.refKey FROM OperationNomenclature n")
    Set<String> findAllRefKeys();
}
