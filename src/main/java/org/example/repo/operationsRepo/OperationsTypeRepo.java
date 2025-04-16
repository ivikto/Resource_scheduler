package org.example.repo.operationsRepo;

import org.example.entity.operationsType.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationsTypeRepo extends JpaRepository<OperationType, Integer> {

    boolean existsByRefKeyAndNameAndTime(String refKey, String nomenclatureName, double time);
}
