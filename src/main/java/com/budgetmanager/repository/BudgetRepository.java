package com.budgetmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.budgetmanager.model.Budget;
import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {
    
    Optional<Budget> findByUserIdAndPeriod(Long userId, String period);
    
    List<Budget> findAllByUserId(Long userId);
}
