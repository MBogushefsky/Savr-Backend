package com.bogaware.savr.repositories;

import com.bogaware.savr.models.TwilioMessage;
import com.bogaware.savr.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TwilioMessageRepository extends JpaRepository<TwilioMessage, String> {
}
