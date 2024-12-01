package com.kazimm.loanapi.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<LoanApiUser, Long> {
    Optional<LoanApiUser> findByUsername(String username);
}
