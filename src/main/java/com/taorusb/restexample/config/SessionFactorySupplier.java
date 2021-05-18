package com.taorusb.restexample.config;

import com.taorusb.restexample.model.Event;
import com.taorusb.restexample.model.File;
import com.taorusb.restexample.model.User;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class SessionFactorySupplier {

    private static SessionFactory sessionFactory;

    private SessionFactorySupplier() {
    }

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = new Configuration()
                    .addAnnotatedClass(User.class)
                    .addAnnotatedClass(File.class)
                    .addAnnotatedClass(Event.class)
                    .buildSessionFactory();
        }
        return sessionFactory;
    }
}