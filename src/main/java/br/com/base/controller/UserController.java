package br.com.base.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.base.records.UserRecordInput;
import br.com.base.records.UserRecordOutput;
import br.com.base.services.UserService;
import jakarta.validation.Valid;

@RestController()
@RequestMapping("/api/v1/user")
public class UserController {
	
	private final UserService userService;
	
	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("{id}")
	public ResponseEntity<Object> getById(@PathVariable Long id) {
		return userService.getById(id);
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity<Object> delete(@PathVariable Long id) {
		return userService.delete(id);
	}

	@PutMapping("{id}")
	public ResponseEntity<Object> update(@PathVariable Long id, @ModelAttribute @Valid UserRecordInput record) {
		return userService.update(id, record);
	}
	
	@GetMapping
	public PagedModel<UserRecordOutput> get(Pageable pageable) {
		return userService.get(pageable);
	}
	
}