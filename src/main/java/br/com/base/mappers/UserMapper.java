package br.com.base.mappers;

import br.com.base.domain.User;
import br.com.base.records.UserResponse;

public class UserMapper {
	
	private UserMapper() {
	
	}
	
	public static UserResponse toUserResponse(User user) {
		return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRoles(), user.getCreationDate());
	}
	
}