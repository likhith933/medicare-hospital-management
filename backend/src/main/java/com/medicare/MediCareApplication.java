package com.medicare;

import com.medicare.entity.Admin;
import com.medicare.repository.AdminRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class MediCareApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediCareApplication.class, args);
    }

    @Bean
    public CommandLineRunner seedData(AdminRepository adminRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (adminRepository.count() == 0) {
                Admin admin = Admin.builder()
                        .name("System Admin")
                        .email("admin@medicare.com")
                        .password(passwordEncoder.encode("AdminPass123!"))
                        .role("ROLE_ADMIN")
                        .build();
                adminRepository.save(admin);
                System.out.println("Default admin user created: admin@medicare.com / AdminPass123!");
            }
        };
    }
}
