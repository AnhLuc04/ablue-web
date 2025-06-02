package com.ablueit.ecommerce.service;

import com.ablueit.ecommerce.model.User;

import java.util.List;

public interface UserService {
    User save(User user);
    User findByUsername(String username);
    User findById(Long id);

    void deleteById(Long id);
    List<User> findAll();
    List<User> findByRoles(String role);

    List<User> findAllSupUsers();
}
