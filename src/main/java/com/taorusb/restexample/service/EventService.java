package com.taorusb.restexample.service;

import com.taorusb.restexample.model.Event;

import java.util.List;

public interface EventService extends GenericService<Event, Long> {
    List<Event> getByUserId(Long id);
    Event getByDoubleId(Long userId, Long id);
}