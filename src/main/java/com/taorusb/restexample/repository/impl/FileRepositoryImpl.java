package com.taorusb.restexample.repository.impl;

import com.taorusb.restexample.supplier.SessionFactorySupplier;
import com.taorusb.restexample.model.File;
import com.taorusb.restexample.model.User;
import com.taorusb.restexample.repository.FileRepository;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;
import java.util.Objects;

public class FileRepositoryImpl implements FileRepository {

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
        try (Session session = SessionFactorySupplier.getSession()) {
            file = session.get(File.class, id);
            if (Objects.isNull(file)) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        }
        return file;
    }

    @Override
    public void deleteById(Long id) {
        int result;
        Transaction transaction;
        try (Session session = SessionFactorySupplier.getSession()) {
            transaction = session.beginTransaction();
            Query query = session.createQuery("delete from File where id = :id");
            query.setParameter("id", id);
            result = query.executeUpdate();
            if (result != 1) {
                throw new ObjectNotFoundException(id, "Entity not found");
            }
            transaction.commit();
        }
    }

    @Override
    public List<File> findAll() {
        List<File> files;
        try (Session session = SessionFactorySupplier.getSession()) {
            files = session.createQuery("from File").list();
        }
        return files;
    }

    @Override
    public File save(File entity) {
        User user;
        Transaction transaction;
        try (Session session = SessionFactorySupplier.getSession()) {
            transaction = session.beginTransaction();
            user = session.get(User.class, entity.getUser().getId());
            if (Objects.isNull(user)) {
                throw new ObjectNotFoundException(entity.getUser().getId(), "Entity not found.");
            }
            session.save(entity);
            transaction.commit();
        }
        return entity;
    }

    @Override
    public File update(File entity) {
        File file;
        User user;
        Transaction transaction;
        try (Session session = SessionFactorySupplier.getSession()) {
            transaction = session.beginTransaction();
            file = session.load(File.class, entity.getId());
            user = session.get(User.class, entity.getUser().getId());
            if (Objects.isNull(file) || Objects.isNull(user)) {
                throw new ObjectNotFoundException(entity.getId(), "Entity not found.");
            }
            file.setPath(entity.getPath());
            file.setUser(user);
            transaction.commit();
        }
        return entity;
    }

    @Override
    public List<File> findByUserId(Long id) {
        List<File> files;
        try (Session session = SessionFactorySupplier.getSession()) {
            files = session.createQuery("from File f where f.user.id = :id")
                    .setParameter("id", id)
                    .list();
            if (files.size() == 0) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        }
        return files;
    }

    @Override
    public File findByDoubleId(Long userId, Long id) {
        File file;
        try (Session session = SessionFactorySupplier.getSession()) {
            file = (File) session.createQuery("from File f where f.user.id = :userId and f.id = :id")
                    .setParameter("userId", userId)
                    .setParameter("id", id)
                    .uniqueResult();
            if (Objects.isNull(file)) {
                throw new ObjectNotFoundException(id, "Entity not found.");
            }
        }
        return file;
    }
}