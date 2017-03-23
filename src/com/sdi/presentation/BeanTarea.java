package com.sdi.presentation;

import java.io.Serializable;

import javax.faces.bean.*;
import javax.faces.event.ActionEvent;

import com.sdi.dto.Task;


@ManagedBean(name = "tarea")
@SessionScoped
public class BeanTarea extends Task implements Serializable {
	
	private static final long serialVersionUID = 55556L;

	public BeanTarea() {
		iniciaTarea(null);
	}

	// Este método es necesario para copiar la tarea a crear/ editar
	public void setTask(Task task) {
		
		setId(task.getId());
		setTitle(task.getTitle());
		setComments(task.getComments());
		setCreated(task.getCreated());
		setPlanned(task.getPlanned());
		setFinished(task.getFinished());
		setCategoryId(task.getCategoryId());
		setUserId(task.getUserId());
		
	}

	// Iniciamos los datos de la tarea con los valores por defecto
	// extraídos del archivo de propiedades correspondiente
	public void iniciaTarea(ActionEvent event) {
		/*FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = facesContext.getApplication()
				.getResourceBundle(facesContext, "msgs");*/
		setId(null);
		//setEmail(bundle.getString("valorDefectoCorreo"));
	}
}
