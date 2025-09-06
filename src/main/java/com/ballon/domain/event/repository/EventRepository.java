package com.ballon.domain.event.repository;

import com.ballon.domain.event.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event,Long>, CustomEventRepository {
}
