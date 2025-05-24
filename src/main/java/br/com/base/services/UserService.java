package br.com.base.services;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.base.domain.User;
import br.com.base.mappers.UserMapper;
import br.com.base.records.UserRequest;
import br.com.base.records.UserResponse;
import br.com.base.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public ResponseEntity<Object> save(UserRequest userRequest) {
		if (userRepository.existsByEmail(userRequest.email())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already used");
		}
		User user = User.builder()
				.name(userRequest.name())
				.email(userRequest.email())
				.roles(userRequest.roles())
				.password(passwordEncoder.encode(userRequest.password()))
				.build();
		userRepository.save(user);
		log.info("Usuario criado com sucesso");
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	public ResponseEntity<Object> getById(Long id) {
		Optional<User> optional = userRepository.findById(id);

		if (optional.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toUserResponse(optional.get()));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
		}
	}

	public ResponseEntity<Object> delete(Long id) {
		Optional<User> optional = userRepository.findById(id);

		if (optional.isPresent()) {
			userRepository.deleteById(id);
			log.info("Usuario deletado com sucesso");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	}

	public ResponseEntity<Object> update(Long id, UserRequest userRequest) {
		Optional<User> optional = userRepository.findById(id);

		if (optional.isPresent()) {
			User user = optional.get();
			user.setName(userRequest.name());
			user.setEmail(userRequest.email());
			user.setRoles(userRequest.roles());
//			user.setPassword(passwordEncoder.encode(userRequest.password()));
			
			userRepository.save(user);

			log.info("Usuario atualizado com sucesso");
			return ResponseEntity.status(HttpStatus.OK).build();
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	}

	public ResponseEntity<Object> get(Pageable pageable) {
		Page<User> pageOfEntities = userRepository.findAll(pageable);
		Page<UserResponse> pageOfRecords = pageOfEntities.map(user -> UserMapper.toUserResponse(user));
		return ResponseEntity.status(HttpStatus.OK).body(new PagedModel<>(pageOfRecords));
	}

}