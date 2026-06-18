package com.talentconnect.service;
import com.talentconnect.dto.PageDto;
import com.talentconnect.dto.UserDto;
import com.talentconnect.entity.User;
import com.talentconnect.exception.DuplicateResourceException;
import com.talentconnect.exception.ResourceNotFoundException;
import com.talentconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
@Service @RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    public PageDto<UserDto> findAll(Pageable p){ return PageDto.from(userRepository.findAll(p),UserDto::from); }
    public UserDto findById(Long id){ return UserDto.from(getOrThrow(id)); }
    public UserDto findByEmail(String email){ return UserDto.from(userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("Utilisateur introuvable: "+email))); }
    @Transactional
    public UserDto create(Map<String,Object> body){
        String email=(String)body.get("email");
        if(userRepository.existsByEmail(email)) throw new DuplicateResourceException("Email deja utilise: "+email);
        long count=userRepository.count();
        User user=User.builder().employeeId("EMP-"+String.format("%04d",count+1)).firstName((String)body.get("firstName")).lastName((String)body.get("lastName")).email(email).password(passwordEncoder.encode((String)body.get("password"))).role(User.Role.valueOf((String)body.getOrDefault("role","EMPLOYEE"))).department((String)body.get("department")).location((String)body.get("location")).title((String)body.get("title")).build();
        return UserDto.from(userRepository.save(user));
    }
    @Transactional
    public UserDto updateProfile(Long userId,Map<String,Object> body){
        User user=getOrThrow(userId);
        if(body.containsKey("firstName")) user.setFirstName((String)body.get("firstName"));
        if(body.containsKey("lastName")) user.setLastName((String)body.get("lastName"));
        if(body.containsKey("department")) user.setDepartment((String)body.get("department"));
        if(body.containsKey("location")) user.setLocation((String)body.get("location"));
        if(body.containsKey("title")) user.setTitle((String)body.get("title"));
        if(body.containsKey("avatarUrl")) user.setAvatarUrl((String)body.get("avatarUrl"));
        if(body.containsKey("experienceYears")) user.setExperienceYears((Integer)body.get("experienceYears"));
        return UserDto.from(userRepository.save(user));
    }
    @Transactional
    public void delete(Long id){ if(!userRepository.existsById(id)) throw new ResourceNotFoundException("Utilisateur introuvable: "+id); userRepository.deleteById(id); }
    public User getOrThrow(Long id){ return userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Utilisateur introuvable: "+id)); }
    public User getByEmail(String email){ return userRepository.findByEmail(email).orElseThrow(()->new ResourceNotFoundException("Utilisateur introuvable: "+email)); }
}
