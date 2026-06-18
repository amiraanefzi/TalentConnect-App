package com.talentconnect.entity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity @Table(name="users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(unique=true,nullable=false,length=20) private String employeeId;
    @Column(nullable=false,length=100) private String firstName;
    @Column(nullable=false,length=100) private String lastName;
    @Column(unique=true,nullable=false,length=255) private String email;
    @Column(nullable=false) private String password;
    @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Role role;
    @Column(length=100) private String department;
    @Column(length=100) private String location;
    @Column(length=150) private String title;
    private int experienceYears;
    @Column(length=500) private String avatarUrl;
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="user_languages",joinColumns=@JoinColumn(name="user_id"))
    @Column(name="language",length=50) @Builder.Default private List<String> languages=new ArrayList<>();
    @ElementCollection(fetch=FetchType.EAGER)
    @CollectionTable(name="user_skills",joinColumns=@JoinColumn(name="user_id"))
    @Column(name="skill",length=100) @Builder.Default private List<String> skills=new ArrayList<>();
    @CreationTimestamp @Column(name="created_at",updatable=false) private LocalDateTime createdAt;
    @UpdateTimestamp @Column(name="updated_at") private LocalDateTime updatedAt;
    public enum Role { EMPLOYEE, HR, ADMIN }
}
