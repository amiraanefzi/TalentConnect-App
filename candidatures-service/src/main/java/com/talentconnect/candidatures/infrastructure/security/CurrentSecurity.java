package com.talentconnect.candidatures.infrastructure.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentSecurity {

	public Long userId() {
		return principal().userId();
	}

	public String role() {
		return principal().role();
	}

	private AuthenticatedUser principal() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
			throw new IllegalStateException("Utilisateur non authentifie");
		}
		return user;
	}
}
