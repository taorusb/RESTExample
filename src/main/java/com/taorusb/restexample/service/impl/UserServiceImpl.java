package com.taorusb.restexample.service.impl;

import com.taorusb.restexample.model.User;
import com.taorusb.restexample.repository.UserRepository;
import com.taorusb.restexample.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getById(Long id) {
        return userRepository.getById(id);
    }

    @Override
    public User update(User user) {
        return userRepository.update(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }
}
