package com.bogaware.savr.repositories;

import com.bogaware.savr.models.UserPreferenceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPreferenceTypeRepository extends JpaRepository<UserPreferenceType, String> {
}
