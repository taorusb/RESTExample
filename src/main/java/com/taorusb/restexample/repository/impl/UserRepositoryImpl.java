package com.taorusb.restexample.repository.impl;

import com.taorusb.restexample.config.SessionFactorySupplier;
import com.taorusb.restexample.model.User;
import com.taorusb.restexample.repository.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class UserRepositoryImpl implements UserRepository {

    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;
    private static UserRepositoryImpl instance;

    private UserRepositoryImpl() {
    }

    public static UserRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new UserRepositoryImpl();
        }
        return instance;
    }

    @Override
    public User getById(Long id) {
        User user;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            user = session.get(User.class, id);
            if (user == null) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        } finally {
            session.close();
        }
        return user;
    }

    @Override
    public void deleteById(Long id) {
        int result;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from User where id = :id");
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
    public List<User> findAll() {
        List<User> users;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            users = session.createQuery("from User").list();
        } finally {
            session.close();
        }
        return users;
    }

    @Override
    public User save(User entity) {
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        } finally {
            session.close();
        }
        return entity;
    }

    @Override
    public User update(User entity) {
        User user;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            user = session.get(User.class, entity.getId());
            if (user == null) {
                throw new ObjectNotFoundException(entity.getId(), "Entity not found.");
            }
            user.setUsername(entity.getUsername());
            user.setStatus(entity.getStatus());
            transaction.commit();
        } finally {
            session.close();
        }
        return entity;
    }
}