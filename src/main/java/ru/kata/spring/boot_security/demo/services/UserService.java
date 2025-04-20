package ru.kata.spring.boot_security.demo.services;

import org.springframework.data.repository.query.Param;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;


import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
public interface UserService {


    User add(User user);

    boolean update(User user);

    boolean removeById(Long id);

    List<User> findAll();

    Optional<User> findById(Long id);
    Optional<User> findUserAndFetchRoles(@Param("email") String email);
}