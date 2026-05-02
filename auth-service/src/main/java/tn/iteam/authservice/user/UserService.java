package tn.iteam.authservice.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.iteam.authservice.kafka.UserEventPublisher;

import java.util.Optional;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Optional<UserEventPublisher> userEventPublisher;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            Optional<UserEventPublisher> userEventPublisher
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEventPublisher = userEventPublisher;
    }

    @Transactional
    public User register(String email, String rawPassword, Set<Role> roles) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        User user = new User();
        user.setEmail(email.toLowerCase());
        user.setPasswordHash(passwordEncoder.encode(rawPassword));
        user.getRoles().addAll(roles);
        User saved = userRepository.save(user);
        userEventPublisher.ifPresent(publisher -> publisher.publishUserCreated(saved));
        return saved;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public List<User> listAll() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateRoles(Long userId, Set<Role> roles) {
        User user = getById(userId);
        user.getRoles().clear();
        user.getRoles().addAll(roles);
        return user;
    }

    @Transactional
    public User setEnabled(Long userId, boolean enabled) {
        User user = getById(userId);
        user.setEnabled(enabled);
        return user;
    }

    @Transactional
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}
