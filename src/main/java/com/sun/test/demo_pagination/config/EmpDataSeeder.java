package com.sun.test.demo_pagination.config;


import com.sun.test.demo_pagination.model.Employee;
import com.sun.test.demo_pagination.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmpDataSeeder implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {
        if (employeeRepository.count() == 0) {
            seedEmployees();
        }
    }

    private void seedEmployees() {
        List<Employee> employees = Arrays.asList(
                Employee.builder()
                        .id(1001L)
                        .firstName("Arun")
                        .lastName("Kumar")
                        .email("arun.k@portal.exe")
                        .mobile("9876543210")
                        .gender(Employee.Gender.MALE)
                        .dob(LocalDate.of(1990, 5, 15))
                        .age(35)
                        .yearsOfExp(12.5)
                        .skills(Arrays.asList("Java", "Spring Boot", "MySQL"))
                        .city("Chennai")
                        .country("India")
                        .securedPecentageInLastDegree(82.5)
                        .address("123 Tech Park, OMR")
                        .build(),

                Employee.builder()
                        .id(1002L)
                        .firstName("Priya")
                        .lastName("Sharma")
                        .email("priya.s@portal.exe")
                        .mobile("9123456789")
                        .gender(Employee.Gender.FEMALE)
                        .dob(LocalDate.of(1995, 8, 22))
                        .age(30)
                        .yearsOfExp(8.0)
                        .skills(Arrays.asList("React", "Next.js", "TypeScript"))
                        .city("Bangalore")
                        .country("India")
                        .securedPecentageInLastDegree(91.0)
                        .address("Whitefield Main Road")
                        .build(),

                Employee.builder()
                        .id(1003L)
                        .firstName("David")
                        .lastName("Miller")
                        .email("david.m@portal.exe")
                        .mobile("8877665544")
                        .gender(Employee.Gender.MALE)
                        .dob(LocalDate.of(1988, 12, 10))
                        .age(37)
                        .yearsOfExp(15.0)
                        .skills(Arrays.asList("AWS", "Docker", "Kubernetes", "Redis"))
                        .city("Hyderabad")
                        .country("India")
                        .securedPecentageInLastDegree(75.0)
                        .address("HITEC City, Phase 2")
                        .build()
        );

        employeeRepository.saveAll(employees);
        System.out.println(">> TAG-CASE#5: Employee Registry Seeded Successfully.");
    }
}
