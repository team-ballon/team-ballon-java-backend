package com.ballon.domain.event.repository;

import com.ballon.domain.event.entity.EventApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventApplicationRepository extends JpaRepository<EventApplication, Long>, CustomEventApplicationRepository {

}
