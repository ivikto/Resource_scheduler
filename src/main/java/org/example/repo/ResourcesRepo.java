package org.example.repo;

import org.example.entity.Resources;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResourcesRepo extends JpaRepository<Resources, Integer> {

    public Resources findFirstByName(String name);
}
