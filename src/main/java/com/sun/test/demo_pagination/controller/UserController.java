package com.sun.test.demo_pagination.controller;
import com.sun.test.demo_pagination.model.User;
import com.sun.test.demo_pagination.model.dto.MemberDTO;
import com.sun.test.demo_pagination.repository.UserRepository;
import com.sun.test.demo_pagination.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/api/v1/users")
// TAG-CASE#5: Required to prevent CORS errors from http://localhost:3000
@CrossOrigin(origins = "http://localhost:3000")
//@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST})
public class UserController {

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private UserService userService;

    @PostMapping(value = "/member")
    public ResponseEntity<MemberDTO> saveMember(@RequestBody MemberDTO request) {
        MemberDTO dto = userService.memberSynch(request);

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity<User> saveUser(@RequestBody User user) {
        // Set current time if not handled by DB default
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }

        User savedUser = userRepository.save(user);
        // Return 201 Created status
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
    @GetMapping
    public Page<User> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sort) {

        // 1. Logic for dynamic sorting (e.g., "name,asc" or "createdAt,desc")
        List<Sort.Order> orders = new ArrayList<>();

        if (sort != null && sort.contains(",")) {
            String[] sortParts = sort.split(",");
            String property = sortParts[0];
            String direction = sortParts[1];

            if (direction.equalsIgnoreCase("asc")) {
                orders.add(Sort.Order.asc(property));
            } else {
                orders.add(Sort.Order.desc(property));
            }
        } else {
            // Default sort by ID descending if no sort provided
            orders.add(Sort.Order.desc("id"));
        }

        // 2. Build the Pageable object
        Pageable pageable = PageRequest.of(page, size, Sort.by(orders));

        // 3. Handle Search vs. Fetch All
        if (search != null && !search.trim().isEmpty()) {
            // Searches across Name or Email (matches your React Global Search)
            return userRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search,
                    search,
                    pageable
            );
        }

        return userRepository.findAll(pageable);
    }
}