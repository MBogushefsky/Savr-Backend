package com.bogaware.savr.repositories.bank;

import com.bogaware.savr.models.bank.PlaidToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlaidTokenRepository extends JpaRepository<PlaidToken, String> {
}
