package br.com.base.mappers;

import br.com.base.domain.User;
import br.com.base.records.LoginRecordOutput;
import br.com.base.records.UserRecordOutput;

public class UserMapper {
	
	private UserMapper() {
	
	}
	
	public static UserRecordOutput toRecord(User user) {
		return new UserRecordOutput(user.getId(), user.getName(), user.getCreationDate());
	}
	
	public static LoginRecordOutput toLoginOutput(User user, String sessionToken) {
		return new LoginRecordOutput(user.getId(), user.getName(), user.getCreationDate(), sessionToken);
	}
	
}