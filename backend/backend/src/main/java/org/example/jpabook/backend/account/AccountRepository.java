package org.example.jpabook.backend.account;

import org.springframework.data.repository.Repository;

import java.util.Optional;

public interface AccountRepository extends Repository<Account, Long> {
    Optional<Account> findByEmail(String email);
}
