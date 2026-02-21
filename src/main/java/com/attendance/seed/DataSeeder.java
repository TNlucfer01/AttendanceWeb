package com.attendance.seed;

import com.attendance.model.SystemConfig;
import com.attendance.model.User;
import com.attendance.model.enums.UserRole;
import com.attendance.repository.SystemConfigRepository;
import com.attendance.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UserRepository userRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public DataSeeder(UserRepository userRepository,
            SystemConfigRepository systemConfigRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        seedAdminUser();
        seedSystemConfig();
    }

    private void seedAdminUser() {
        if (!userRepository.existsByRole(UserRole.ADMIN)) {
            User admin = User.builder()
                    .email(adminEmail)
                    .passwordHash(passwordEncoder.encode(adminPassword))
                    .role(UserRole.ADMIN)
                    .isActive(true)
                    .mustChangePassword(true)
                    .build();

            userRepository.save(admin);
            log.info("✅ Admin account seeded: {}", adminEmail);
        } else {
            log.info("ℹ️  Admin account already exists, skipping seed.");
        }
    }

    private void seedSystemConfig() {
        if (systemConfigRepository.count() == 0) {
            SystemConfig config = SystemConfig.builder()
                    .periodsPerDay(7)
                    .defaultAttendanceThreshold(75.0)
                    .maxLeaveLimitPerSemester(15)
                    .maxOdDaysPerSemester(10)
                    .build();

            systemConfigRepository.save(config);
            log.info("✅ System config seeded with defaults.");
        } else {
            log.info("ℹ️  System config already exists, skipping seed.");
        }
    }
}
