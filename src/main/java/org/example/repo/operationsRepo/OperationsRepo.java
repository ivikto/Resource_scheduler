package org.example.repo.operationsRepo;

import org.example.entity.operationsType.LaserCutter;
import org.example.entity.operationsType.OperationType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationsRepo extends JpaRepository<OperationType, Integer> {
}
