package com.sdi.presentation;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.sdi.model.Alumno;

@ManagedBean(name="alumno")
@SessionScoped
public class BeanAlumno extends Alumno implements Serializable {

	private static final long serialVersionUID = 55556L;
	
	public BeanAlumno() {
		iniciaAlumno(null);
	}
	
	//Este método es necesario para copiar el alumno a editar cuando
	//se pincha el enlace Editar en la vista listado.xhtml. Podría sustituirse
	//por un método editar en BeanAlumnos.
	public void setAlumno(Alumno alumno) {
		setId(alumno.getId());
		setIduser(alumno.getIduser());
		setNombre(alumno.getNombre());
		setApellidos(alumno.getApellidos());
		setEmail(alumno.getEmail());
	}
	
	//Iniciamos los datos del alumno con los valores por defecto
	//extraídos del archivo de propiedades correspondiente
	@SuppressWarnings("unused")
	public void iniciaAlumno(ActionEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle =
		facesContext.getApplication().getResourceBundle(facesContext, "msgs");
	}

}
