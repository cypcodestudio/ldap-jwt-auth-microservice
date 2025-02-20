package com.cypcode.ldap_jwt_auth_microservice.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.cypcode.ldap_jwt_auth_microservice.domain.Role;
import com.cypcode.ldap_jwt_auth_microservice.domain.User;
import com.cypcode.ldap_jwt_auth_microservice.domain.UserRole;
import com.cypcode.ldap_jwt_auth_microservice.domain.dto.UserRegisterRequestDTO;
import com.cypcode.ldap_jwt_auth_microservice.repository.IUserRepository;
import com.cypcode.ldap_jwt_auth_microservice.repository.IUserRoleRepository;
import com.cypcode.ldap_jwt_auth_microservice.security.SecurityPrincipal;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class WUserService implements UserDetailsService {
	private static final Logger LOG = LoggerFactory.getLogger(WUserService.class);

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private IUserRoleRepository userRoleRepository;

	@Autowired
	WRoleService roleService;

	@Override
	public UserDetails loadUserByUsername(String username) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			List<UserRole> userRoles = userRoleRepository.findAllByUserId(user.getId());

			Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

			userRoles.forEach(userRole -> {
				authorities.add(new SimpleGrantedAuthority(userRole.getRole().getName()));
			});

			UserDetails principal = new org.springframework.security.core.userdetails.User(user.getUsername(),
					user.getPassword(), authorities);

			return principal;
		}
		return null;
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public String createUser(UserRegisterRequestDTO request) {
		try {
			User user = (User) dtoMapperRequestDtoToUser(request);

			user = userRepository.save(user);
			if (!request.getRoleList().isEmpty()) {
				for (String role : request.getRoleList()) {
					Role existingRole = roleService.findRoleByName("ROLE_" + role.toUpperCase());
					if(existingRole != null) {
						addUserRole(user, existingRole);
					}
				}
			} else {
				addUserRole(user, null);
			}

			return "User successfully created.";
		} catch (Exception e) {
			e.printStackTrace();
			return e.getCause().getMessage();
		}

	}

	public List<User> retrieveAllUserList() {
		return userRepository.findAll();
	}

	public User updateUser(UserRegisterRequestDTO userRequestDTO) {

		User user = (User) dtoMapperRequestDtoToUser(userRequestDTO);

		user = userRepository.save(user);
		addUserRole(user, null);

		return user;
	}

	public User findCurrentUser() {
		return userRepository.findById(SecurityPrincipal.getInstance().getLoggedInPrincipal().getId()).get();

	}

	public List<UserRole> findAllCurrentUserRole() {
		return userRoleRepository.findAllByUserId(SecurityPrincipal.getInstance().getLoggedInPrincipal().getId());

	}

	public Optional<User> findUserById(long id) {
		return userRepository.findById(id);
	}

	public void addUserRole(User user, Role role) {

		UserRole userRole = new UserRole();
		userRole.setUser(user);

		if (role == null) {
			role = roleService.findDefaultRole();
		}
		
		userRole.setRole(role);
		userRoleRepository.save(userRole);
	}

	private Object dtoMapperRequestDtoToUser(UserRegisterRequestDTO source) {
		User target = new User();
		target.setEntityNo(source.getEntityNo());
		target.setUsername(source.getUsername());
		target.setPassword(source.getPassword());

		return target;
	}

}