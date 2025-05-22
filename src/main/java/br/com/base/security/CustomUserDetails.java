package br.com.base.security;

import java.io.Serial;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.base.domain.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

	@Serial
	private static final long serialVersionUID = 7367206920779498219L;
	private final User user;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return user.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getEmail();
	}
	
}