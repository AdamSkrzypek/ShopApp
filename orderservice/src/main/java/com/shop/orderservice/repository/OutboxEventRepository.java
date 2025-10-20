package com.shop.orderservice.repository;

import com.shop.orderservice.domain.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    @Query(value = "SELECT * FROM outbox_events ORDER BY created_at ASC LIMIT 10 FOR UPDATE SKIP LOCKED", nativeQuery = true)
    List<OutboxEvent> findAndLockOldestEventsToProcess();
}
