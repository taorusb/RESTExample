package com.taorusb.restexample.service.impl;

import com.taorusb.restexample.model.Event;
import com.taorusb.restexample.repository.EventRepository;
import com.taorusb.restexample.service.EventService;

import java.util.List;

public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    public EventServiceImpl(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Override
    public Event getById(Long id) {
        return eventRepository.getById(id);
    }

    @Override
    public Event update(Event event) {
        return eventRepository.update(event);
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

    @Override
    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    @Override
    public List<Event> getAll() {
        return eventRepository.findAll();
    }

    @Override
    public List<Event> getByUserId(Long id) {
        return eventRepository.findByUserId(id);
    }

    @Override
    public Event getByUserId(Long userId, Long id) {
        return eventRepository.findByDoubleId(userId, id);
    }
}