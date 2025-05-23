package br.com.base.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.base.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	public Boolean existsByEmail(String email);
	public Optional<User> findByEmail(String email);
	
}