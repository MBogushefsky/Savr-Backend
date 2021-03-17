package com.bogaware.savr.repositories;

import com.bogaware.savr.models.PlaidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaidTokenRepository extends JpaRepository<PlaidToken, String> {
}
