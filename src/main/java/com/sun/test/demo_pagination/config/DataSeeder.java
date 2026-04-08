package com.sun.test.demo_pagination.config;

import com.sun.test.demo_pagination.model.User;
import com.sun.test.demo_pagination.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository repository) {
        return args -> {
            // Check if data already exists to avoid duplicates on restart
            if (repository.count() == 0) {
                System.out.println("TAG-CASE#5: Seeding initial user data into PostgreSQL...");

                List<String> firstNames = Arrays.asList("James", "Mary", "Robert", "Patricia", "John", "Jennifer", "Michael", "Linda");
                List<String> lastNames = Arrays.asList("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis");
                List<String> roles = Arrays.asList("Administrator", "Editor", "Viewer", "Manager");
                List<String> statuses = Arrays.asList("Active", "Inactive");

                Random random = new Random();

                for (int i = 1; i <= 50; i++) {
                    String firstName = firstNames.get(random.nextInt(firstNames.size()));
                    String lastName = lastNames.get(random.nextInt(lastNames.size()));

                    User user = new User();
                    user.setName(firstName + " " + lastName);
                    user.setEmail(firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@enterprise.com");
                    user.setRole(roles.get(random.nextInt(roles.size())));
                    user.setStatus(statuses.get(random.nextInt(statuses.size())));
                    user.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));

                    repository.save(user);
                }

                System.out.println("Successfully seeded 50 users.");
            } else {
                System.out.println("Database already contains data. Skipping seeding.");
            }
        };
    }
}
