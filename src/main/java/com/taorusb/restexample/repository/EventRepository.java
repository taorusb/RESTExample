package com.taorusb.restexample.repository;

import com.taorusb.restexample.model.Event;

import java.util.List;

public interface EventRepository extends GenericRepository<Event, Long> {
    List<Event> findByUserId(Long id);
    Event findByDoubleId(Long userId, Long id);
}
