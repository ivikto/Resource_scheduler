package org.example.repo;

import org.example.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourcesRepo extends JpaRepository<Resources, Integer> {
}
