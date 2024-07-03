package br.com.base.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.base.domain.User;
import br.com.base.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicationStartupListener {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public ApplicationStartupListener(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@EventListener
	public void onApplicationEvent(ContextRefreshedEvent event) {
		if (userRepository.count() == 0) {
			log.info("Criando usuário admin do sistema");
			
			User user = User.builder()
					.name("Base")
					.email("base@base.com")
					.password(passwordEncoder.encode("base1234"))
					.build();
			
			userRepository.save(user);
		}
	}
}