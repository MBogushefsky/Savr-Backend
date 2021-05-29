package com.bogaware.savr.repositories.bank;

import com.bogaware.savr.models.bank.Goal;
import com.bogaware.savr.models.bank.PlaidTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, String> {
    @Query(value = "SELECT g FROM Goal g WHERE UserID = :userId ORDER BY CreatedDate DESC")
    List<Goal> findAllByUserId(@Param("userId") String userId);

    @Query(value = "SELECT g FROM Goal g WHERE ID = :goalId AND UserID = :userId ORDER BY CreatedDate DESC")
    Goal findAllByGoalIdAndUserId(@Param("goalId") String goalId, @Param("userId") String userId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM goal WHERE ID = :goalId AND UserID = :userId", nativeQuery = true)
    void deleteAllByIdAndUserId(@Param("goalId") String goalId, @Param("userId") String userId);
}
