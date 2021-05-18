package com.taorusb.restexample.repository.impl;

import com.taorusb.restexample.config.SessionFactorySupplier;
import com.taorusb.restexample.model.Event;
import com.taorusb.restexample.model.File;
import com.taorusb.restexample.model.User;
import com.taorusb.restexample.repository.EventRepository;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class EventRepositoryImpl implements EventRepository {

    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;
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
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            event = session.get(Event.class, id);
            if (event == null) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        } finally {
            session.close();
        }
        return event;
    }

    @Override
    public void deleteById(Long id) {
        int result;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from Event where id = :id");
            query.setParameter("id", id);
            result = query.executeUpdate();
            if (result != 1) {
                throw new ObjectNotFoundException(id, "Entity not found");
            }
            transaction.commit();
        } finally {
            session.close();
        }
    }

    @Override
    public List<Event> findAll() {
        List<Event> events;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            events = session.createQuery("from Event").list();
        } finally {
            session.close();
        }
        return events;
    }

    @Override
    public Event save(Event entity) {
        User user;
        File file;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            user = session.get(User.class, entity.getUser().getId());
            file = session.get(File.class, entity.getFile().getId());
            if (user == null || file == null) {
                throw new ObjectNotFoundException(entity.getUser().getId(), "Entity not found");
            }
            session.save(entity);
            transaction.commit();
        } finally {
            session.close();
        }
        return entity;
    }

    @Override
    public Event update(Event entity) {
        Event event;
        User user;
        File file;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            event = session.load(Event.class, entity.getId());
            user = session.load(User.class, entity.getUser().getId());
            file = session.load(File.class, entity.getFile().getId());
            if (event == null || user == null || file == null) {
                throw new ObjectNotFoundException(entity.getId(), "Entity not found.");
            }
            event.setUploadDate(entity.getUploadDate());
            event.setUser(user);
            event.setFile(file);
            transaction.commit();
        } finally {
            session.close();
        }
        return entity;
    }

    @Override
    public List<Event> findByUserId(Long id) {
        List<Event> events;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            events = session.createQuery("from Event where user.id = :id")
                    .setParameter("id", id)
                    .list();
            if (events.size() == 0) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        } finally {
            session.close();
        }
        return events;
    }

    @Override
    public Event findByDoubleId(Long userId, Long id) {
        Event event;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            event = (Event) session.createQuery("from Event e where e.user.id = :userId and e.id = :id")
                    .setParameter("userId", userId)
                    .setParameter("id", id)
                    .uniqueResult();
            if (event == null) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        } finally {
            session.close();
        }
        return event;
    }
}