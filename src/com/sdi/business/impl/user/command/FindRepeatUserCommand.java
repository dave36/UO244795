package com.sdi.business.impl.user.command;

import com.sdi.business.exception.BusinessException;
import com.sdi.business.impl.command.Command;
import com.sdi.dto.User;
import com.sdi.persistence.Persistence;

public class FindRepeatUserCommand implements Command<User> {
	
	private String user;
	
	public FindRepeatUserCommand(String user){
		this.user=user;
	}

	@Override
	public User execute() throws BusinessException {
		
		User repetido = Persistence.getUserDao().findByLogin(user);
		return repetido;
	}

}
