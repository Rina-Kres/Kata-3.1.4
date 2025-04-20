package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;
import ru.kata.spring.boot_security.demo.repositories.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public User add(User user) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional <User> userOptional =  userRepository.findUserAndFetchRoles(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Убедитесь, что роли загружены
            Set<Role> roles = user.getRoles();
            if (roles == null || roles.isEmpty()) {
                throw new UsernameNotFoundException("User roles not found for email: " + email);
            }
            return user;
        } else {
            throw new UsernameNotFoundException("User not found for email: " + email);
        }
    }
    @Override
    @Transactional
    public boolean update(User user) {

        return userRepository.findById(user.getId())
                .map(entity -> {
                    entity.setEmail(user.getEmail());
                    entity.setFirstName(user.getFirstName());
                    entity.setLastName(user.getLastName());
                    entity.setAge(user.getAge());
                    if (user.getPassword() != null) {
                        entity.setPassword(user.getPassword());
                    }
                    entity.setRoles(user.getRoles());
                    return true;
                })
                .orElse(false);
    }

    @Override
    @Transactional
    public boolean removeById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return true;
                })
                .orElse(false);
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findUserAndFetchRoles(String email){
        return userRepository.findUserAndFetchRoles(email);
    }
}