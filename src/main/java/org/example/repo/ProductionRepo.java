package org.example.repo;

import org.example.entity.Production;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionRepo extends JpaRepository<Production, Integer> {
}
