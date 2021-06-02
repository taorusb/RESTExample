package com.taorusb.restexample.repository.impl;

import com.taorusb.restexample.supplier.SessionFactorySupplier;
import com.taorusb.restexample.model.Event;
import com.taorusb.restexample.model.File;
import com.taorusb.restexample.model.User;
import com.taorusb.restexample.repository.EventRepository;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Objects;

public class EventRepositoryImpl implements EventRepository {

    private static EventRepositoryImpl instance;

    private EventRepositoryImpl() {
    }

    public static EventRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new EventRepositoryImpl();
        }
        return instance;
    }

    @Override
    public Event getById(Long id) {
        Event event;
        try (Session session = SessionFactorySupplier.getSession()) {
            event = session.get(Event.class, id);
            if (Objects.isNull(event)) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        }
        return event;
    }

    @Override
    public void deleteById(Long id) {
        int result;
        try (Session session = SessionFactorySupplier.getSession()) {
            Transaction transaction = session.beginTransaction();
            Query query = session.createQuery("delete from Event where id = :id");
            query.setParameter("id", id);
            result = query.executeUpdate();
            if (result != 1) {
                throw new ObjectNotFoundException(id, "Entity not found");
            }
            transaction.commit();
        }
    }

    @Override
    public List<Event> findAll() {
        List<Event> events;
        try (Session session = SessionFactorySupplier.getSession()) {
            events = session.createQuery("from Event").list();
        }
        return events;
    }

    @Override
    public Event save(Event entity) {
        User user;
        File file;
        try (Session session = SessionFactorySupplier.getSession()) {
            Transaction transaction = session.beginTransaction();
            user = session.get(User.class, entity.getUser().getId());
            file = session.get(File.class, entity.getFile().getId());
            if (Objects.isNull(user) || Objects.isNull(file)) {
                throw new ObjectNotFoundException(entity.getUser().getId(), "Entity not found");
            }
            session.save(entity);
            transaction.commit();
        }
        return entity;
    }

    @Override
    public Event update(Event entity) {
        Event event;
        User user;
        File file;
        try (Session session = SessionFactorySupplier.getSession()) {
            Transaction transaction = session.beginTransaction();
            event = session.load(Event.class, entity.getId());
            user = session.load(User.class, entity.getUser().getId());
            file = session.load(File.class, entity.getFile().getId());
            if (Objects.isNull(event) || Objects.isNull(user) || Objects.isNull(file)) {
                throw new ObjectNotFoundException(entity.getId(), "Entity not found.");
            }
            event.setUploadDate(entity.getUploadDate());
            event.setUser(user);
            event.setFile(file);
            transaction.commit();
        }
        return entity;
    }

    @Override
    public List<Event> findByUserId(Long id) {
        List<Event> events;
        try (Session session = SessionFactorySupplier.getSession()) {
            events = session.createQuery("from Event where user.id = :id")
                    .setParameter("id", id)
                    .list();
            if (events.size() == 0) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        }
        return events;
    }

    @Override
    public Event findByDoubleId(Long userId, Long id) {
        Event event;
        try (Session session = SessionFactorySupplier.getSession()) {
            event = (Event) session.createQuery("from Event e where e.user.id = :userId and e.id = :id")
                    .setParameter("userId", userId)
                    .setParameter("id", id)
                    .uniqueResult();
            if (Objects.isNull(event)) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        }
        return event;
    }
}