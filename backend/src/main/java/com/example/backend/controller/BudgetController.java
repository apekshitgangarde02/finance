package com.example.backend.controller;

import com.example.backend.entity.Budget;
import com.example.backend.entity.User;
import com.example.backend.repository.BudgetRepository;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/budgets")
public class BudgetController {
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private UserRepository userRepository;

    // Add Budget
    @PostMapping
    public ResponseEntity<?> addBudget(@RequestBody BudgetRequest request) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Budget budget = new Budget();
        budget.setUser(user);
        budget.setYear(request.getYear());
        budget.setMonth(request.getMonth());
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budgetRepository.save(budget);
        return ResponseEntity.ok(new BudgetResponse(budget));
    }

    // List Budgets (all for user)
    @GetMapping
    public ResponseEntity<?> listBudgets() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        List<Budget> budgets = budgetRepository.findByUser(user);
        List<BudgetResponse> response = budgets.stream().map(BudgetResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // List Budgets for a month/year
    @GetMapping("/month")
    public ResponseEntity<?> listBudgetsForMonth(@RequestParam int year, @RequestParam int month) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        List<Budget> budgets = budgetRepository.findByUserAndYearAndMonth(user, year, month);
        List<BudgetResponse> response = budgets.stream().map(BudgetResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Update Budget
    @PutMapping("/{id}")
    public ResponseEntity<?> updateBudget(@PathVariable Long id, @RequestBody BudgetRequest request) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<Budget> optionalBudget = budgetRepository.findById(id);
        if (optionalBudget.isEmpty() || !optionalBudget.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body("Budget not found");
        }
        Budget budget = optionalBudget.get();
        budget.setYear(request.getYear());
        budget.setMonth(request.getMonth());
        budget.setCategory(request.getCategory());
        budget.setAmount(request.getAmount());
        budgetRepository.save(budget);
        return ResponseEntity.ok(new BudgetResponse(budget));
    }

    // Delete Budget
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBudget(@PathVariable Long id) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<Budget> optionalBudget = budgetRepository.findById(id);
        if (optionalBudget.isEmpty() || !optionalBudget.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body("Budget not found");
        }
        budgetRepository.delete(optionalBudget.get());
        return ResponseEntity.ok("Budget deleted successfully");
    }

    // Helper to get current user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    // DTOs
    public static class BudgetRequest {
        private int year;
        private int month;
        private String category;
        private BigDecimal amount;
        public int getYear() { return year; }
        public void setYear(int year) { this.year = year; }
        public int getMonth() { return month; }
        public void setMonth(int month) { this.month = month; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
    }
    public static class BudgetResponse {
        private Long id;
        private int year;
        private int month;
        private String category;
        private BigDecimal amount;
        public BudgetResponse(Budget budget) {
            this.id = budget.getId();
            this.year = budget.getYear();
            this.month = budget.getMonth();
            this.category = budget.getCategory();
            this.amount = budget.getAmount();
        }
        public Long getId() { return id; }
        public int getYear() { return year; }
        public int getMonth() { return month; }
        public String getCategory() { return category; }
        public BigDecimal getAmount() { return amount; }
    }
} 