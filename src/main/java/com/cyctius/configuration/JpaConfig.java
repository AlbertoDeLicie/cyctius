package com.cyctius.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
    // This class is used to enable JPA auditing features in the application.
    // It allows the application to automatically populate createdAt and updatedAt fields
    // in entities that extend the Auditable class.

    // No additional configuration is needed here, as the @EnableJpaAuditing annotation
    // takes care of enabling the auditing features.
}