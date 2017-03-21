package com.sdi.business.impl.admin.command;

import java.util.Calendar;
import java.util.Date;

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

	@Override
	public Void execute() throws BusinessException {
		UserDao uDao = Persistence.getUserDao();
		CategoryDao cDao = Persistence.getCategoryDao();
		TaskDao tDao = Persistence.getTaskDao();
		
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 6);
		
		tDao.deleteAllTasks();
		cDao.deleteAllCategories();
		uDao.deleteAllUser();
		
		for(int i=1; i<4; i++){
			User user = new User();
			String login="user"+i;
			user.setLogin(login);
			user.setPassword(login);
			user.setEmail(login+"@mail.com");
			Long idUser = uDao.save(user);
			
			crearTareasSinCategoria(tDao, idUser);
			
			for(int j=1; j<4; j++){
				Category c = new Category();
				String name="categoria"+j;
				c.setName(name);
				c.setUserId(idUser);
				Long idCat = cDao.save(c);
				
				crearTareasPorCategoria(tDao, idCat, idUser, j);
			}
		}
		
		return null;
	}

	private void crearTareasPorCategoria(TaskDao tDao, Long idCat, Long idUser, int j) {
		Calendar calendar = Calendar.getInstance();

		if(j==1){
			calendar.add(Calendar.DAY_OF_MONTH, -4);
			for(int k=21; k<24; k++){
				Task t = new Task();
				String namet = "Tarea"+k;
				t.setTitle(namet);
				t.setPlanned(calendar.getTime());
				t.setUserId(idUser);
				t.setCategoryId(idCat);
				tDao.save(t);
			}
		}
		else if(j==2){
			calendar.add(Calendar.DAY_OF_MONTH, -6);
			for(int k=24; k<27; k++){
				Task t = new Task();
				String namet = "Tarea"+k;
				t.setTitle(namet);
				t.setPlanned(calendar.getTime());
				t.setUserId(idUser);
				t.setCategoryId(idCat);
				tDao.save(t);
			}
		}
		else{
			calendar.add(Calendar.DAY_OF_MONTH, -9);
			for(int k=27; k<31; k++){
				Task t = new Task();
				String namet = "Tarea"+k;
				t.setTitle(namet);
				t.setPlanned(calendar.getTime());
				t.setUserId(idUser);
				t.setCategoryId(idCat);
				tDao.save(t);
			}
		}
	}

	private void crearTareasSinCategoria(TaskDao tDao, Long idUser) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 6);
		
		for(int i=1; i<21; i++){
			if(i<11){
				Task t = new Task();
				String name = "Tarea"+i;
				t.setTitle(name);
				t.setPlanned(calendar.getTime());
				t.setUserId(idUser);
				tDao.save(t);
			}
			else{
				Task t = new Task();
				String name = "Tarea"+i;
				t.setTitle(name);
				t.setPlanned(new Date());
				t.setUserId(idUser);
				tDao.save(t);
			}
		}
	}
}