package com.taorusb.restexample.repository.impl;

import com.taorusb.restexample.supplier.SessionFactorySupplier;
import com.taorusb.restexample.model.User;
import com.taorusb.restexample.repository.UserRepository;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Objects;

public class UserRepositoryImpl implements UserRepository {

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
        try (Session session = SessionFactorySupplier.getSession()) {
            user = session.get(User.class, id);
            if (Objects.isNull(user)) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        }
        return user;
    }

    @Override
    public void deleteById(Long id) {
        int result;
        Transaction transaction;
        try (Session session = SessionFactorySupplier.getSession()) {
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from User where id = :id");
            query.setParameter("id", id);
            result = query.executeUpdate();
            if (result != 1) {
                throw new ObjectNotFoundException(id, "Entity not found");
            }
            transaction.commit();
        }
    }

    @Override
    public List<User> findAll() {
        List<User> users;
        try (Session session = SessionFactorySupplier.getSession()) {
            users = session.createQuery("from User").list();
        }
        return users;
    }

    @Override
    public User save(User entity) {
        Transaction transaction;
        try (Session session = SessionFactorySupplier.getSession()) {
            transaction = session.beginTransaction();
            session.save(entity);
            transaction.commit();
        }
        return entity;
    }

    @Override
    public User update(User entity) {
        User user;
        Transaction transaction;
        try (Session session = SessionFactorySupplier.getSession()) {
            transaction = session.beginTransaction();
            user = session.get(User.class, entity.getId());
            if (Objects.isNull(user)) {
                throw new ObjectNotFoundException(entity.getId(), "Entity not found.");
            }
            user.setUsername(entity.getUsername());
            user.setStatus(entity.getStatus());
            transaction.commit();
        }
        return entity;
    }
}