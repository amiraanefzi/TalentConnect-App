package com.talentconnect.repository;
import com.talentconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);
    List<User> findByRole(User.Role role);
    boolean existsByEmail(String email);
    boolean existsByEmployeeId(String employeeId);
}
