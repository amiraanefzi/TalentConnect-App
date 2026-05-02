package tn.iteam.authservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tn.iteam.authservice.user.Role;
import tn.iteam.authservice.user.UserRepository;
import tn.iteam.authservice.user.UserService;

import java.util.Set;

@Configuration
@EnableConfigurationProperties(BootstrapAdminConfig.BootstrapAdminProperties.class)
public class BootstrapAdminConfig {
    @Bean
    CommandLineRunner bootstrapAdmin(BootstrapAdminProperties props, UserRepository userRepository, UserService userService) {
        return args -> {
            if (!props.enabled()) {
                return;
            }
            if (props.email() == null || props.email().isBlank() || props.password() == null || props.password().isBlank()) {
                return;
            }
            if (!userRepository.existsByEmailIgnoreCase(props.email())) {
                userService.register(props.email(), props.password(), Set.of(Role.ADMIN));
            }
        };
    }

    @ConfigurationProperties(prefix = "app.bootstrap.admin")
    public record BootstrapAdminProperties(
            boolean enabled,
            String email,
            String password
    ) {
    }
}

