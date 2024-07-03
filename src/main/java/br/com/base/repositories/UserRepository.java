package br.com.base.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.base.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {

	@Query(value = "SELECT * FROM User u WHERE BINARY u.email = :email", nativeQuery = true)
	public Optional<User> findByEmail(String email);
	public Boolean existsByEmail(String email);
	
}