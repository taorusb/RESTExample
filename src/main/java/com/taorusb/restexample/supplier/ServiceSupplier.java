package com.taorusb.restexample.supplier;

import com.taorusb.restexample.repository.EventRepository;
import com.taorusb.restexample.repository.FileRepository;
import com.taorusb.restexample.repository.UserRepository;
import com.taorusb.restexample.repository.impl.EventRepositoryImpl;
import com.taorusb.restexample.repository.impl.FileRepositoryImpl;
import com.taorusb.restexample.repository.impl.UserRepositoryImpl;
import com.taorusb.restexample.service.impl.EventServiceImpl;
import com.taorusb.restexample.service.impl.FileServiceImpl;
import com.taorusb.restexample.service.impl.UserServiceImpl;

public class ServiceSupplier {

    private static ServiceSupplier instance;
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final EventRepository eventRepository;
    private UserServiceImpl userService;
    private FileServiceImpl fileService;
    private EventServiceImpl eventService;

    private ServiceSupplier() {
        userRepository = UserRepositoryImpl.getInstance();
        fileRepository = FileRepositoryImpl.getInstance();
        eventRepository = EventRepositoryImpl.getInstance();
        userService = new UserServiceImpl(userRepository);
        fileService = new FileServiceImpl(fileRepository);
        eventService = new EventServiceImpl(eventRepository);
    }

    public static ServiceSupplier getInstance() {
        if (instance == null) {
            instance = new ServiceSupplier();
        }
        return instance;
    }

    public UserServiceImpl getUserService() {
        return userService;
    }

    public FileServiceImpl getFileService() {
        return fileService;
    }

    public EventServiceImpl getEventService() {
        return eventService;
    }
}
