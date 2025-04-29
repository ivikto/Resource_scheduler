package org.example.repo;

import org.example.entity.timeline.ScheduledOperation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScheduledOperationRepo extends JpaRepository<ScheduledOperation, Long> {

    Optional<ScheduledOperation> findByResourceId(String resourceId);

}
