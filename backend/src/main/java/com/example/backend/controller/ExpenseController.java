package com.example.backend.controller;

import com.example.backend.entity.Expense;
import com.example.backend.entity.User;
import com.example.backend.repository.ExpenseRepository;
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
@RequestMapping("/api/expenses")
public class ExpenseController {
    @Autowired
    private ExpenseRepository expenseRepository;
    @Autowired
    private UserRepository userRepository;

    // Add Expense
    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseRequest request) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expenseRepository.save(expense);
        return ResponseEntity.ok(new ExpenseResponse(expense));
    }

    // List Expenses
    @GetMapping
    public ResponseEntity<?> listExpenses() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        List<Expense> expenses = expenseRepository.findByUser(user);
        List<ExpenseResponse> response = expenses.stream().map(ExpenseResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Update Expense
    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@PathVariable Long id, @RequestBody ExpenseRequest request) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isEmpty() || !optionalExpense.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body("Expense not found");
        }
        Expense expense = optionalExpense.get();
        expense.setAmount(request.getAmount());
        expense.setCategory(request.getCategory());
        expense.setDescription(request.getDescription());
        expense.setDate(request.getDate());
        expenseRepository.save(expense);
        return ResponseEntity.ok(new ExpenseResponse(expense));
    }

    // Delete Expense
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<Expense> optionalExpense = expenseRepository.findById(id);
        if (optionalExpense.isEmpty() || !optionalExpense.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body("Expense not found");
        }
        expenseRepository.delete(optionalExpense.get());
        return ResponseEntity.ok("Expense deleted successfully");
    }

    // Helper to get current user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    // DTOs
    public static class ExpenseRequest {
        private BigDecimal amount;
        private String category;
        private String description;
        private LocalDate date;
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
    }
    public static class ExpenseResponse {
        private Long id;
        private BigDecimal amount;
        private String category;
        private String description;
        private LocalDate date;
        public ExpenseResponse(Expense expense) {
            this.id = expense.getId();
            this.amount = expense.getAmount();
            this.category = expense.getCategory();
            this.description = expense.getDescription();
            this.date = expense.getDate();
        }
        public Long getId() { return id; }
        public BigDecimal getAmount() { return amount; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public LocalDate getDate() { return date; }
    }
} 