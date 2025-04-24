package org.example.repo;

import org.example.entity.Operation;
import org.example.entity.Production;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OperationRepo extends JpaRepository<Operation, Integer> {

    public List<Operation> getByProduction(Production production);

}
