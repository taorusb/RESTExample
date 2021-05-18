package com.taorusb.restexample.repository.impl;

import com.taorusb.restexample.config.SessionFactorySupplier;
import com.taorusb.restexample.model.File;
import com.taorusb.restexample.model.User;
import com.taorusb.restexample.repository.FileRepository;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class FileRepositoryImpl implements FileRepository {

    private SessionFactory sessionFactory;
    private Session session;
    private Transaction transaction;
    private static FileRepositoryImpl instance;

    private FileRepositoryImpl() {
    }

    public static FileRepositoryImpl getInstance() {
        if (instance == null) {
            instance = new FileRepositoryImpl();
        }
        return instance;
    }

    @Override
    public File getById(Long id) {
        File file;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            file = session.get(File.class, id);
            if (file == null) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        } finally {
            session.close();
        }
        return file;
    }

    @Override
    public void deleteById(Long id) {
        int result;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from File where id = :id");
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
    public List<File> findAll() {
        List<File> files;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            files = session.createQuery("from File").list();
        } finally {
            session.close();
        }
        return files;
    }

    @Override
    public File save(File entity) {
        User user;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            user = session.get(User.class, entity.getUser().getId());
            if (user == null) {
                throw new ObjectNotFoundException(entity.getUser().getId(), "Entity not found.");
            }
            session.save(entity);
            transaction.commit();
        } finally {
            session.close();
        }
        return entity;
    }

    @Override
    public File update(File entity) {
        File file;
        User user;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            file = session.load(File.class, entity.getId());
            user = session.get(User.class, entity.getUser().getId());
            if (file == null || user == null) {
                throw new ObjectNotFoundException(entity.getId(), "Entity not found.");
            }
            file.setPath(entity.getPath());
            file.setUser(user);
            transaction.commit();
        } finally {
            session.close();
        }
        return entity;
    }

    @Override
    public List<File> findByUserId(Long id) {
        List<File> files;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            files = session.createQuery("from File f where f.user.id = :id")
                    .setParameter("id", id)
                    .list();
            if (files.size() == 0) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        } finally {
            session.close();
        }
        return files;
    }

    @Override
    public File findByDoubleId(Long userId, Long id) {
        File file;
        sessionFactory = SessionFactorySupplier.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            file = (File) session.createQuery("from File f where f.user.id = :userId and f.id = :id")
                    .setParameter("userId", userId)
                    .setParameter("id", id)
                    .uniqueResult();
            if (file == null) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        } finally {
            session.close();
        }
        return file;
    }
}