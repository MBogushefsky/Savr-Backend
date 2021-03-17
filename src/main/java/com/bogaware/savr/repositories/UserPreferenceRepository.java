package com.bogaware.savr.repositories;

import com.bogaware.savr.models.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, String> {

    @Query(value = "SELECT up FROM UserPreference up WHERE UserID = :userId AND Preference = :preference")
    UserPreference findByUserIdAndKey(@Param("userId") String userId, @Param("preference") String preference);
}
