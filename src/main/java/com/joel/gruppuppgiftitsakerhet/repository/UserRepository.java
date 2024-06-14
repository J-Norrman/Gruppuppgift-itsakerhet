package com.joel.gruppuppgiftitsakerhet.repository;

import com.joel.gruppuppgiftitsakerhet.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

//Interface för hantering av AppUser entiteter med hjälp av Spring data JPA.
//Ger oss grundläggande CRUD-funktionalitet samt en metod för att hitta en användare via email.
public interface UserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByemail(String email);
}
