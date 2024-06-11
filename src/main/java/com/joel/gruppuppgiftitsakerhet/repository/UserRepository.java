package com.joel.gruppuppgiftitsakerhet.repository;

import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByemail(String email);
}
