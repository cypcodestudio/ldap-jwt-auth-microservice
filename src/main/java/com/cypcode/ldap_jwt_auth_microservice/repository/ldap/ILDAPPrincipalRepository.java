package com.cypcode.ldap_jwt_auth_microservice.repository.ldap;

import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import com.cypcode.ldap_jwt_auth_microservice.domain.ADPrincipal;


public interface ILDAPPrincipalRepository extends LdapRepository<ADPrincipal>{
	ADPrincipal findByCn(String cn);
	ADPrincipal findByCnAndPassword(String cn, String password);
}
