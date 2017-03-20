package com.sdi.business.impl.admin.command;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sdi.business.exception.BusinessException;
import com.sdi.business.impl.command.Command;
import com.sdi.dto.Category;
import com.sdi.dto.Task;
import com.sdi.dto.User;
import com.sdi.persistence.CategoryDao;
import com.sdi.persistence.Persistence;
import com.sdi.persistence.TaskDao;
import com.sdi.persistence.UserDao;

public class DropAndInsertDB implements Command<Void> {
	
	private List<Long> idsUsers = new ArrayList<>();
	private Map<Long, List<Category>> categorias = new HashMap<>();

	@Override
	public Void execute() throws BusinessException {
		UserDao uDao = Persistence.getUserDao();
		CategoryDao cDao = Persistence.getCategoryDao();
		TaskDao tDao = Persistence.getTaskDao();
		
		tDao.deleteAllTasks();
		cDao.deleteAllCategories();
		uDao.deleteAllUser();
		
		for(int i=1; i<=3; i++){
			User user = new User();
			String login="user"+i;
			user.setLogin(login);
			user.setPassword(login);
			Long idUser = uDao.save(user);
			idsUsers.add(idUser);
		}
		
		for(Long id: idsUsers){
			List<Category> cs = new ArrayList<>();
			for(int i=1; i<=3; i++){
				Category c = new Category();
				String name="categoria"+i + "user"+id;
				c.setName(name);
				c.setUserId(id);
				cDao.save(c);
				cs.add(c);				
			}
			categorias.put(id, cs);
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 6);
		
		for(Long id: idsUsers){
			for(int i=1; i<11; i++){
				Task t = new Task();
				String name = "Tarea"+i;
				t.setTitle(name);
				t.setPlanned(calendar.getTime());
				t.setUserId(id);
				tDao.save(t);
			}
			
			for(int i=11; i<21; i++){
				Task t = new Task();
				String name = "Tarea"+i;
				t.setTitle(name);
				t.setPlanned(new Date());
				t.setUserId(id);
				tDao.save(t);
			}
			
			calendar.add(Calendar.DAY_OF_MONTH, -6);
			
			List<Category> cs = categorias.get(id);
			for(int i=21; i<24; i++){
				Task t = new Task();
				String name = "Tarea"+i;
				t.setTitle(name);
				t.setPlanned(calendar.getTime());
				t.setUserId(id);
				t.setCategoryId(cs.get(0).getId());
				tDao.save(t);
			}
			
			calendar.add(Calendar.DAY_OF_MONTH, -4);
			
			for(int i=24; i<26; i++){
				Task t = new Task();
				String name = "Tarea"+i;
				t.setTitle(name);
				t.setPlanned(calendar.getTime());
				t.setUserId(id);
				t.setCategoryId(cs.get(1).getId());
				tDao.save(t);
			}
			
			calendar.add(Calendar.DAY_OF_MONTH, -9);
			
			for(int i=24; i<26; i++){
				Task t = new Task();
				String name = "Tarea"+i;
				t.setTitle(name);
				t.setPlanned(calendar.getTime());
				t.setUserId(id);
				t.setCategoryId(cs.get(2).getId());
				tDao.save(t);
			}
		}
		
		return null;
	}

}
