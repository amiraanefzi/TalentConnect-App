package tn.iteam.authservice.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmployeeId(String employeeId);

    /** Retourne le numéro le plus élevé d'employeeId (ex: "EMP-0007" → 7) */
    @Query(value = "SELECT COALESCE(MAX(CAST(SUBSTRING(employee_id, 5) AS UNSIGNED)), 0) FROM users WHERE employee_id LIKE 'EMP-%'", nativeQuery = true)
    int findMaxEmployeeIdSequence();
}

