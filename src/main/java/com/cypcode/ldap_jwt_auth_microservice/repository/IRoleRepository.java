package com.cypcode.ldap_jwt_auth_microservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cypcode.ldap_jwt_auth_microservice.domain.Role;



@Repository
public interface IRoleRepository extends JpaRepository<Role, Long>{
	Role findByName(String name);

}