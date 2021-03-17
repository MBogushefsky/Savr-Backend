package com.bogaware.savr.repositories;

import com.bogaware.savr.models.PlaidAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PlaidAccountRepository extends JpaRepository<PlaidAccount, String> {
    @Query(value = "SELECT pa FROM PlaidAccount pa WHERE UserID=:userId")
    List<PlaidAccount> findAllByUserId(@Param("userId") String userId);

    @Query(value = "SELECT pa FROM PlaidAccount pa WHERE AccountID=:accountId")
    PlaidAccount findByAccountId(@Param("accountId") String accountId);
}
