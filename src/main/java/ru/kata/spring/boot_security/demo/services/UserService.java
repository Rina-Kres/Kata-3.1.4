package ru.kata.spring.boot_security.demo.services;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.security.Principal;
import java.util.Collection;
import java.util.List;

public interface UserService {

    User findByUsername(String username);

    List<User> getAllUsers();

    User getUserById(Long id);

    User oneUser(Principal principal);

    boolean createUser(User user);

    boolean editUser(@ModelAttribute("user") User user);

    boolean deleteUser(@PathVariable Long id);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles);

}