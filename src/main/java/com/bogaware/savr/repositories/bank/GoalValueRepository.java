package com.bogaware.savr.repositories.bank;

import com.bogaware.savr.models.bank.Goal;
import com.bogaware.savr.models.bank.GoalValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface GoalValueRepository extends JpaRepository<GoalValue, String> {
    @Query(value = "SELECT gv FROM GoalValue gv WHERE GoalID = :goalId")
    List<GoalValue> findAllByGoalId(@Param("goalId") String goalId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM goal_value WHERE GoalID = :goalId", nativeQuery = true)
    void deleteAllByGoalId(@Param("goalId") String goalId);
}
