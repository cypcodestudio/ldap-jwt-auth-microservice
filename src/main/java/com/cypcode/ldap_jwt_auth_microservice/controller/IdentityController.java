package com.cypcode.ldap_jwt_auth_microservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cypcode.ldap_jwt_auth_microservice.domain.ADPrincipal;
import com.cypcode.ldap_jwt_auth_microservice.domain.dto.AuthenticationRequest;
import com.cypcode.ldap_jwt_auth_microservice.domain.dto.AuthenticationResponse;
import com.cypcode.ldap_jwt_auth_microservice.domain.dto.EntityResponse;
import com.cypcode.ldap_jwt_auth_microservice.domain.dto.UserRegisterRequestDTO;
import com.cypcode.ldap_jwt_auth_microservice.repository.ldap.ILDAPPrincipalRepository;
import com.cypcode.ldap_jwt_auth_microservice.security.JWTTokenUtil;
import com.cypcode.ldap_jwt_auth_microservice.service.WUserService;


@RestController
@RequestMapping("identity")
public class IdentityController {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	WUserService userService;

	@Autowired
	private JWTTokenUtil jwtTokenUtil;

	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	private ILDAPPrincipalRepository ldaPrincipalRepository;

	@PostMapping("/token")
	public ResponseEntity<Object> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest)
			throws Exception {

		try {
			authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
		} catch (Exception e) {
			return EntityResponse.generateResponse("Authentication", HttpStatus.UNAUTHORIZED,
					"Invalid credentials, please check details and try again.");
		}
		final UserDetails userDetails = userService.loadUserByUsername(authenticationRequest.getUsername());

		final String token = jwtTokenUtil.generateToken(userDetails);
		final String refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

		return EntityResponse.generateResponse("Authentication", HttpStatus.OK,
				new AuthenticationResponse(token, refreshToken));

	}

	private void authenticate(String username, String password) throws Exception {
		try {
			ADPrincipal principal = ldaPrincipalRepository.findByCn(username);
			if(principal == null) {
				throw new Exception("Invalid credentials");
			}
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}catch(Exception e) {
			throw new Exception("INVALID_CREDENTIALS", e.getCause());
			
		}
	}
	
	@PostMapping("register")
	public ResponseEntity<Object> register(@RequestBody UserRegisterRequestDTO request){
		request.setPassword(passwordEncoder.encode(request.getPassword()));
		return EntityResponse.generateResponse("Regsiter User", HttpStatus.OK, userService.createUser(request));
	}

	@GetMapping("profile")
	public ResponseEntity<Object> retrieveUserProfile(){
		return EntityResponse.generateResponse("User Profile", HttpStatus.OK, userService.findCurrentUser());
	}
}
