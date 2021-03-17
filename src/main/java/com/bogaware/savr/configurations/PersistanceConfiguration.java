package com.bogaware.savr.configurations;

import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Configuration
public class PersistanceConfiguration {

    @PersistenceContext
    private EntityManager entityManager;
}
