package com.example.backend.repository;

import com.example.backend.entity.Budget;
import com.example.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    List<Budget> findByUserAndYearAndMonth(User user, int year, int month);
    List<Budget> findByUser(User user);
} 