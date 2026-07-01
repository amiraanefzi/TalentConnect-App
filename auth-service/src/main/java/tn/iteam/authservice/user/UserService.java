package tn.iteam.authservice.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.iteam.authservice.kafka.UserEventPublisher;
import tn.iteam.authservice.user.dto.ExperienceDto;
import tn.iteam.authservice.user.dto.FormationDto;
import tn.iteam.authservice.user.dto.UpdateProfileRequest;
import tn.iteam.authservice.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Optional<UserEventPublisher> userEventPublisher;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       Optional<UserEventPublisher> userEventPublisher, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userEventPublisher = userEventPublisher;
        this.objectMapper = objectMapper;
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
        // Génération robuste de l'employeeId : basée sur le MAX existant, pas sur count()
        int nextSeq = userRepository.findMaxEmployeeIdSequence() + 1;
        String employeeId = "EMP-" + String.format("%04d", nextSeq);
        // Sécurité anti-collision (cas rare de concurrence)
        while (userRepository.existsByEmployeeId(employeeId)) {
            nextSeq++;
            employeeId = "EMP-" + String.format("%04d", nextSeq);
        }
        user.setEmployeeId(employeeId);
        User saved = userRepository.save(user);
        userEventPublisher.ifPresent(publisher -> publisher.publishUserCreated(saved));
        return saved;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + email));
    }

    public User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable: " + id));
    }

    public List<User> listAll() { return userRepository.findAll(); }

    @Transactional
    public User updateRoles(Long userId, Set<Role> roles) {
        User user = getById(userId);
        user.getRoles().clear();
        user.getRoles().addAll(roles);
        return userRepository.save(user);
    }

    @Transactional
    public User setEnabled(Long userId, boolean enabled) {
        User user = getById(userId);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    @Transactional
    public void delete(Long userId) { userRepository.deleteById(userId); }

    /** Mise à jour du profil (utilisateur lui-même ou admin) */
    @Transactional
    public User updateProfile(Long userId, UpdateProfileRequest req) {
        User user = getById(userId);
        if (req.firstName()        != null) user.setFirstName(req.firstName());
        if (req.lastName()         != null) user.setLastName(req.lastName());
        if (req.department()       != null) user.setDepartment(req.department());
        if (req.location()         != null) user.setLocation(req.location());
        if (req.title()            != null) user.setTitle(req.title());
        if (req.experienceYears()  != null) user.setExperienceYears(req.experienceYears());
        if (req.avatarUrl()        != null) user.setAvatarUrl(req.avatarUrl());
        if (req.phone()            != null) user.setPhone(req.phone());
        if (req.address()          != null) user.setAddress(req.address());
        if (req.bio()              != null) user.setBio(req.bio());
        if (req.linkedinUrl()      != null) user.setLinkedinUrl(req.linkedinUrl());
        if (req.githubUrl()        != null) user.setGithubUrl(req.githubUrl());
        if (req.languages()        != null) { user.getLanguages().clear(); user.getLanguages().addAll(req.languages()); }
        if (req.skills()           != null) { user.getSkills().clear(); user.getSkills().addAll(req.skills()); }
        if (req.formations()       != null) user.setFormationsJson(toJson(req.formations()));
        if (req.experiences()      != null) user.setExperiencesJson(toJson(req.experiences()));
        return userRepository.save(user);
    }

    /** Conversion User → UserDto avec désérialisation JSON */
    public UserDto toDto(User user) {
        return new tn.iteam.authservice.user.dto.UserDto(
                user.getId(), user.getEmployeeId(), user.getEmail(),
                user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()),
                user.isEnabled(), user.getCreatedAt(),
                user.getFirstName(), user.getLastName(), user.getDepartment(),
                user.getLocation(), user.getTitle(), user.getExperienceYears(),
                user.getAvatarUrl(), user.getPhone(), user.getAddress(), user.getBio(),
                user.getLinkedinUrl(), user.getGithubUrl(),
                user.getLanguages(), user.getSkills(),
                fromJson(user.getFormationsJson(), new TypeReference<List<FormationDto>>() {}),
                fromJson(user.getExperiencesJson(), new TypeReference<List<ExperienceDto>>() {})
        );
    }

    private String toJson(Object obj) {
        try { return objectMapper.writeValueAsString(obj); }
        catch (Exception e) { return "[]"; }
    }

    private <T> List<T> fromJson(String json, TypeReference<List<T>> ref) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try { return objectMapper.readValue(json, ref); }
        catch (Exception e) { return new ArrayList<>(); }
    }
}


