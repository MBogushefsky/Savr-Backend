package com.bogaware.savr.repositories.user;

import com.bogaware.savr.models.user.TwilioMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TwilioMessageRepository extends JpaRepository<TwilioMessage, String> {
}
