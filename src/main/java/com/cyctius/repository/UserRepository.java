package com.cyctius.repository;

import com.cyctius.entity.CyctiusUser;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<CyctiusUser, String> {
    Optional<CyctiusUser> findByUsername(String username);
    Boolean existsByUsername(String username);
    Boolean existsByEmail(String email);
    Optional<CyctiusUser> findByEmail(String email);
    Optional<CyctiusUser> findByIssuerId(String issuerId);
}
