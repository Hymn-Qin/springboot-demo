package com.example.demo.service.impl;

import com.example.demo.data.model.RegisterUser;
import com.example.demo.exception.*;
import com.example.demo.model.Role;
import com.example.demo.model.User;
import com.example.demo.repos.UserRepository;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository repository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(RegisterUser register) {
        Optional<User> optionalUser = repository.findByUsername(register.getUsername());
        if (optionalUser.isPresent()) {
            throw new UsernameAlreadyExistException("User name already exist!Please choose another user name.");
        }
        String password = passwordEncoder.encode(register.getPassword());
        User user = new User();
        user.setUsername(register.getUsername());
        user.setPassword(password);
        user.getRoles().add(new Role(Role.USER));
//        user.getRoles().add(new Role(Role.ADMIN));
//        user.getRoles().add(new Role(Role.DEV));

        return Optional.of(repository.save(user))
                .orElseThrow(() -> new SQLException("this  " + user + " insert exception"));
    }

    @Override
    public User findByUserId(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with id " + id));
    }

    @Override
    public User findUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with username " + username));
    }

    @Override
    public void deleteUserByUserId(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public User updateUser(User user) {
        return null;
    }
}
