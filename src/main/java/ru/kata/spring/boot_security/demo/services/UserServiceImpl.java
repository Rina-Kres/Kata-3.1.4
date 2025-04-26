package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.RoleRepository;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.security.Principal;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserDetailsService, UserService {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           @Lazy PasswordEncoder passwordEncoder,
                           RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }


    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User getUserById(Long id) {
        return null;
    }

    @Override
    public User oneUser(Principal principal) {
        return null;
    }

    @Override
    public boolean createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        setUserRoles(user);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean editUser(User user) {
        return userRepository.findById(user.getId())
                .map(existingUser -> {
                    userRepository.findByEmail(user.getEmail())
                            .ifPresent(userWithSameEmail -> {
                                Long existingId = existingUser.getId();
                                Long newId = user.getId();
                                if (existingId != null && newId != null && !existingId.equals(newId)) {
                                    throw new IllegalArgumentException("Email already in use");
                                }
                            });

                    existingUser.setUsername(user.getUsername());
                    existingUser.setLastName(user.getLastName());
                    existingUser.setAge(user.getAge());
                    existingUser.setEmail(user.getEmail());

                    if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
                    }

                    setUserRoles(user);
                    userRepository.save(existingUser);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public boolean deleteUser(Long id) {
        return false;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return null;
    }

    private void setUserRoles(User user) {
        Set<Role> managedRoles = new HashSet<>();
        for (Role role : user.getRoles()) {
            roleRepository.findById(role.getId())
                    .ifPresent(managedRoles::add);
        }
        user.setRoles(managedRoles);
    }
}