package br.com.base.config;

import java.util.Set;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.base.domain.User;
import br.com.base.repositories.UserRepository;
import br.com.base.security.Roles;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationStartupListener {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (userRepository.count() == 0) {
			log.info("No users found in the database. Creating default admin user...");

			User user = User.builder()
					.name("Base")
					.email("base@base.com")
					.password(passwordEncoder.encode("base1234"))
					.roles(Set.of(Roles.ADMIN))
					.build();

			userRepository.save(user);
		}
	}

}