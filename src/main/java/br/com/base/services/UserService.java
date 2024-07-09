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
import br.com.base.records.UserRecordInput;
import br.com.base.records.UserRecordOutput;
import br.com.base.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public ResponseEntity<Object> save(UserRecordInput record) {
		if (userRepository.existsByEmail(record.email())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already used");
		}
		User user = User.builder()
				.name(record.name())
				.email(record.email())
				.password(passwordEncoder.encode(record.password()))
				.build();
		userRepository.save(user);
		log.info("Usuario criado com sucesso");
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	public ResponseEntity<Object> getById(Long id) {
		
		Optional<User> optional = userRepository.findById(id);

		if (optional.isPresent()) {
			return ResponseEntity.status(HttpStatus.OK).body(UserMapper.toRecord(optional.get()));
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

	public ResponseEntity<Object> update(Long id, UserRecordInput record) {
		Optional<User> optional = userRepository.findById(id);

		if (optional.isPresent()) {
			User user = optional.get();
			user.setName(record.name());
			user.setEmail(record.email());
			user.setPassword(passwordEncoder.encode(record.password()));
			
			userRepository.save(user);

			log.info("Usuario atualizado com sucesso");
			return ResponseEntity.status(HttpStatus.OK).build();
		}

		return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
	}

	public PagedModel<UserRecordOutput> get(Pageable pageable) {
		Page<User> pageOfEntities = userRepository.findAll(pageable);
		Page<UserRecordOutput> pageOfRecords = pageOfEntities.map(user -> UserMapper.toRecord(user));
		return new PagedModel<>(pageOfRecords); 
	}

}