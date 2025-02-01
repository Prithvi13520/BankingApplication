package com.app.banking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.banking.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User,Long>{

    Boolean existsByPanNumber(String panNumber);
    Boolean existsByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);

}
