package com.example.backend.controller;

import com.example.backend.entity.Income;
import com.example.backend.entity.User;
import com.example.backend.repository.IncomeRepository;
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
@RequestMapping("/api/incomes")
public class IncomeController {
    @Autowired
    private IncomeRepository incomeRepository;
    @Autowired
    private UserRepository userRepository;

    // Add Income
    @PostMapping
    public ResponseEntity<?> addIncome(@RequestBody IncomeRequest request) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Income income = new Income();
        income.setUser(user);
        income.setAmount(request.getAmount());
        income.setSource(request.getSource());
        income.setDescription(request.getDescription());
        income.setDate(request.getDate());
        incomeRepository.save(income);
        return ResponseEntity.ok(new IncomeResponse(income));
    }

    // List Incomes
    @GetMapping
    public ResponseEntity<?> listIncomes() {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        List<Income> incomes = incomeRepository.findByUser(user);
        List<IncomeResponse> response = incomes.stream().map(IncomeResponse::new).collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    // Update Income
    @PutMapping("/{id}")
    public ResponseEntity<?> updateIncome(@PathVariable Long id, @RequestBody IncomeRequest request) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<Income> optionalIncome = incomeRepository.findById(id);
        if (optionalIncome.isEmpty() || !optionalIncome.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body("Income not found");
        }
        Income income = optionalIncome.get();
        income.setAmount(request.getAmount());
        income.setSource(request.getSource());
        income.setDescription(request.getDescription());
        income.setDate(request.getDate());
        incomeRepository.save(income);
        return ResponseEntity.ok(new IncomeResponse(income));
    }

    // Delete Income
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable Long id) {
        User user = getCurrentUser();
        if (user == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<Income> optionalIncome = incomeRepository.findById(id);
        if (optionalIncome.isEmpty() || !optionalIncome.get().getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(404).body("Income not found");
        }
        incomeRepository.delete(optionalIncome.get());
        return ResponseEntity.ok("Income deleted successfully");
    }

    // Helper to get current user
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    // DTOs
    public static class IncomeRequest {
        private BigDecimal amount;
        private String source;
        private String description;
        private LocalDate date;
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }
    }
    public static class IncomeResponse {
        private Long id;
        private BigDecimal amount;
        private String source;
        private String description;
        private LocalDate date;
        public IncomeResponse(Income income) {
            this.id = income.getId();
            this.amount = income.getAmount();
            this.source = income.getSource();
            this.description = income.getDescription();
            this.date = income.getDate();
        }
        public Long getId() { return id; }
        public BigDecimal getAmount() { return amount; }
        public String getSource() { return source; }
        public String getDescription() { return description; }
        public LocalDate getDate() { return date; }
    }
} 