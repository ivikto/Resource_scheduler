package org.example.repo.operationsRepo;

import org.example.entity.operationsType.LaserCutter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaserCutterRepo extends JpaRepository<LaserCutter, Integer> {
}
