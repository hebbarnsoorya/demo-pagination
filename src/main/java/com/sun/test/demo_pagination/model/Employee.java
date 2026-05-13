package com.sun.test.demo_pagination.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    private Long id; // Required as per frontend

    @Column(nullable = false)
    private String firstName;

    private String lastName;

    private LocalDate dob;

    @ElementCollection
    @CollectionTable(name = "employee_skills", joinColumns = @JoinColumn(name = "employee_id"))
    @Column(name = "skill")
    private List<String> skills;

    private Double yearsOfExp;

    @Column(nullable = false)
    private String mobile;

    private String email;

    private String altMobile;

    private String altEmail;

    private String bloodGroup;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(columnDefinition = "TEXT")
    private String address;

    private String city;

    private String postalCode;

    private String country;

    private LocalDateTime lastLogin;

    private Integer age;

    private Double securedPecentageInLastDegree;

    public enum Gender {
        MALE, FEMALE, OTHER
    }
}
