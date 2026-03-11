package fr.matteofierquin.springauth.springauth.config;

import fr.matteofierquin.springauth.springauth.model.Role;
import fr.matteofierquin.springauth.springauth.model.User;
import fr.matteofierquin.springauth.springauth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer {

    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initUsers(UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                var admin = User.builder()
                        .username("admin")
                        .email("admin@springsuite.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build();
                userRepository.save(admin);
                System.out.println("Admin user created: admin / admin123");
            }

            if (!userRepository.existsByUsername("user")) {
                var user = User.builder()
                        .username("user")
                        .email("user@springsuite.com")
                        .password(passwordEncoder.encode("user123"))
                        .role(Role.USER)
                        .build();
                userRepository.save(user);
                System.out.println("Test user created: user / user123");
            }
        };
    }
}
