package com.sdi.dto.util;

import com.sdi.dto.Category;
import com.sdi.dto.Task;
import com.sdi.dto.User;


public class Cloner {

	public static User clone(User u) {
		User us = new User();
		us.setId( 		u.getId() );
		us.setEmail( 		u.getEmail() );
		us.setIsAdmin(	u.getIsAdmin() );
		us.setLogin( 		u.getLogin() );
		us.setPassword( 	u.getPassword() );
		us.setStatus( 	u.getStatus() );
		return us;
	}
	
	public static Task clone(Task t) {
		Task task = new Task();
		task.setCategoryId( t.getCategoryId() );
		task.setComments(t.getComments() );
		task.setCreated(t.getCreated() );
		task.setFinished(t.getFinished() );
		task.setId(t.getId() );
		task.setPlanned(t.getPlanned() );
		task.setTitle(t.getTitle() );
		task.setUserId(t.getUserId() );
		return task;
	}

	public static Category clone(Category c) {
		return new Category()
				.setName( 	c.getName() )
				.setUserId( c.getUserId() );
	}

}
