package com.talentconnect.dto;
import com.talentconnect.entity.User;
import java.util.List;
public record UserDto(Long id,String employeeId,String firstName,String lastName,String email,User.Role role,String department,String location,String title,List<String> skills,int experienceYears,String avatarUrl,List<String> languages){
    public static UserDto from(User u){return new UserDto(u.getId(),u.getEmployeeId(),u.getFirstName(),u.getLastName(),u.getEmail(),u.getRole(),u.getDepartment(),u.getLocation(),u.getTitle(),u.getSkills(),u.getExperienceYears(),u.getAvatarUrl(),u.getLanguages());}
}
