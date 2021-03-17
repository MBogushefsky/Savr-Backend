package com.bogaware.savr.repositories;

import com.bogaware.savr.models.PlaidTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface PlaidTransactionRepository extends JpaRepository<PlaidTransaction, String> {
    @Query(value = "SELECT pt FROM PlaidTransaction pt WHERE UserID = :userId ORDER BY Date DESC")
    List<PlaidTransaction> findAllByUserId(@Param("userId") String userId);

    @Query(value = "SELECT pt FROM PlaidTransaction pt WHERE AccountID = :accountId ORDER BY Date DESC")
    List<PlaidTransaction> findAllByAccountId(@Param("accountId") String accountId);

    @Query(value = "SELECT pt FROM PlaidTransaction pt WHERE TransactionID = :transactionId ORDER BY Date DESC")
    PlaidTransaction findByTransactionId(@Param("transactionId") String transactionId);

    @Query(value = "SELECT * FROM PlaidTransaction WHERE UserID = :userId ORDER BY Date DESC LIMIT 5", nativeQuery = true)
    List<PlaidTransaction> findAllLatest5ByUserId(@Param("userId") String userId);

    @Query(value = "SELECT pt FROM PlaidTransaction pt \n" +
            "WHERE UserID = :userId \n" +
            "AND AccountID = :accountId \n" +
            "AND Amount = :amount \n" +
            "AND MerchantName = :merchantName \n" +
            "AND Name = :name \n" +
            "AND Date = :date \n" +
            "ORDER BY Date DESC")
    List<PlaidTransaction> findAllByContent(@Param("userId") String userId,
                                            @Param("accountId") String accountId,
                                            @Param("amount") double amount,
                                            @Param("merchantName") String merchantName,
                                            @Param("name") String name,
                                            @Param("date") Date date);
}
