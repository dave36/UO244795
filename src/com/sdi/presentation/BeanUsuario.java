package com.sdi.presentation;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.sdi.dto.User;


@ManagedBean(name = "usuario")
@SessionScoped
public class BeanUsuario extends User implements Serializable {
	private static final long serialVersionUID = 55556L;

	public BeanUsuario() {
		iniciaUsuario(null);
	}

	// Este método es necesario para copiar el alumno a editar cuando
	// se pincha el enlace Editar en la vista listado.xhtml. Podría sustituirse
	// por un método editar en BeanAlumnos.
	public void setUsuario(User user) {
		setId(user.getId());
		setLogin(user.getLogin());
		setEmail(user.getEmail());
		setPassword(user.getPassword());
		setIsAdmin(user.getIsAdmin());
		setStatus(user.getStatus());
		
	}

	// Iniciamos los datos del alumno con los valores por defecto
	// extraídos del archivo de propiedades correspondiente
	public void iniciaUsuario(ActionEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = facesContext.getApplication()
				.getResourceBundle(facesContext, "msgs");
		setId(null);
		//setLogin(bundle.getString("valorDefectoUserId"));
		//setEmail(bundle.getString("valorDefectoCorreo"));
	}
}
