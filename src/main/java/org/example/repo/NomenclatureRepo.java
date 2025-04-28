package org.example.repo;

import org.example.entity.Nomenclature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface NomenclatureRepo extends JpaRepository<Nomenclature, Integer> {

    @Query("SELECT s.description FROM Nomenclature s WHERE s.refKey = :ref_key")
    String findRefKeyByName(@Param("ref_key") String ref_key);

    @Query("SELECT n.refKey FROM Nomenclature n")
    Set<String> findAllRefKeys();
}
