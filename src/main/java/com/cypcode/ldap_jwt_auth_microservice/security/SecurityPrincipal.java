package com.cypcode.ldap_jwt_auth_microservice.security;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.cypcode.ldap_jwt_auth_microservice.domain.User;
import com.cypcode.ldap_jwt_auth_microservice.service.WUserService;


@Service
public class SecurityPrincipal {
	private static SecurityPrincipal securityPrincipal = null;

	private Authentication principal = SecurityContextHolder.getContext().getAuthentication();

	private static WUserService userService;

	@Autowired
	private SecurityPrincipal(WUserService userService) {
		this.userService = userService;
	}

	public static SecurityPrincipal getInstance() {
		securityPrincipal = new SecurityPrincipal(userService);
		return securityPrincipal;
	}

	public User getLoggedInPrincipal() {
		if (principal != null) {
			UserDetails loggedInPrincipal = (UserDetails) principal.getPrincipal();
			return userService.findByUsername(loggedInPrincipal.getUsername());
		}
		return null;
	}

	public Collection<?> getLoggedInPrincipalAuthorities() {
		return ((UserDetails) principal.getPrincipal()).getAuthorities();
	}

}