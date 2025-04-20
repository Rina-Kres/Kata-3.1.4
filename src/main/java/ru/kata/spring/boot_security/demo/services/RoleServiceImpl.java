package ru.kata.spring.boot_security.demo.services;


import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;

import java.util.ArrayList;
import java.util.List;
@Service
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role add(Role role) {
        return roleRepository.save(role);
    }

    @Override
    public List<Role> findAll() {
        List<Role> roles = roleRepository.findAll();
        return roles.isEmpty() ? new ArrayList<>() : new ArrayList<>(roles);
    }
}