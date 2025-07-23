package com.example.backend.controller;

import com.example.backend.entity.Goal;
import com.example.backend.entity.User;
import com.example.backend.repository.GoalRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/goals")
public class GoalController {
    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private UserRepository userRepository;

    // Add Goal
    @PostMapping
    public ResponseEntity<?> addGoal(@RequestBody GoalRequest request) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Goal goal = new Goal();
        goal.setUser(user);
        goal.setName(request.getName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : BigDecimal.ZERO);
        goal.setTargetDate(request.getTargetDate());
        goal.setDescription(request.getDescription());
        goal.setStatus(request.getStatus());
        goalRepository.save(goal);
        return ResponseEntity.ok(new GoalResponse(goal));
    }

    // List Goals
    @GetMapping
    public ResponseEntity<?> listGoals() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        List<Goal> goals = goalRepository.findByUser(user);
        List<GoalResponse> response = goals.stream().map(GoalResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Update Goal
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGoal(@PathVariable Long id, @RequestBody GoalRequest request) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<Goal> optionalGoal = goalRepository.findById(id);
        if (optionalGoal.isEmpty() || !optionalGoal.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body("Goal not found");
        }
        Goal goal = optionalGoal.get();
        goal.setName(request.getName());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(request.getCurrentAmount() != null ? request.getCurrentAmount() : BigDecimal.ZERO);
        goal.setTargetDate(request.getTargetDate());
        goal.setDescription(request.getDescription());
        goal.setStatus(request.getStatus());
        goalRepository.save(goal);
        return ResponseEntity.ok(new GoalResponse(goal));
    }

    // Delete Goal
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGoal(@PathVariable Long id) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<Goal> optionalGoal = goalRepository.findById(id);
        if (optionalGoal.isEmpty() || !optionalGoal.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body("Goal not found");
        }
        goalRepository.delete(optionalGoal.get());
        return ResponseEntity.ok("Goal deleted successfully");
    }

    // Helper to get current user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    // DTOs
    public static class GoalRequest {
        private String name;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private LocalDate targetDate;
        private String description;
        private String status;
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public BigDecimal getTargetAmount() { return targetAmount; }
        public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }
        public BigDecimal getCurrentAmount() { return currentAmount; }
        public void setCurrentAmount(BigDecimal currentAmount) { this.currentAmount = currentAmount; }
        public LocalDate getTargetDate() { return targetDate; }
        public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    public static class GoalResponse {
        private Long id;
        private String name;
        private BigDecimal targetAmount;
        private BigDecimal currentAmount;
        private LocalDate targetDate;
        private String description;
        private String status;
        public GoalResponse(Goal goal) {
            this.id = goal.getId();
            this.name = goal.getName();
            this.targetAmount = goal.getTargetAmount();
            this.currentAmount = goal.getCurrentAmount();
            this.targetDate = goal.getTargetDate();
            this.description = goal.getDescription();
            this.status = goal.getStatus();
        }
        public Long getId() { return id; }
        public String getName() { return name; }
        public BigDecimal getTargetAmount() { return targetAmount; }
        public BigDecimal getCurrentAmount() { return currentAmount; }
        public LocalDate getTargetDate() { return targetDate; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
    }
} 