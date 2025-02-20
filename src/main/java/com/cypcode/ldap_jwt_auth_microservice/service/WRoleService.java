package com.cypcode.ldap_jwt_auth_microservice.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cypcode.ldap_jwt_auth_microservice.domain.Role;
import com.cypcode.ldap_jwt_auth_microservice.repository.IRoleRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WRoleService {
	private static final Logger LOG = LoggerFactory.getLogger(WRoleService.class);

	@Autowired
	private IRoleRepository roleReposiroty;

	public Role save(Role role) {
		return roleReposiroty.save(role);
	}

	public List<Role> findAllRole() {
		return roleReposiroty.findAll();
	}

	public Role findDefaultRole() {
		return findAllRole().stream().findFirst().orElse(null);
	}

	public Role findRoleByName(String role) {
		return findAllRole().stream().filter(r -> r.getName().equals(role)).findFirst().orElse(null);
	}

}