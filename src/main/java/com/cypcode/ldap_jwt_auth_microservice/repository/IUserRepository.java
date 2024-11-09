package com.cypcode.ldap_jwt_auth_microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cypcode.ldap_jwt_auth_microservice.domain.User;



@Repository
public interface IUserRepository extends JpaRepository<User, Long> {
    User findUserByUsernameAndPassword(String username, String password);
    
    User findByUsername(String username);
}