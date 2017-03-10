package com.sdi.presentation;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.sdi.business.AdminService;
import com.sdi.business.UserService;
import com.sdi.business.exception.BusinessException;
import com.sdi.dto.User;
import com.sdi.infrastructure.Factories;

@ManagedBean(name = "controller")
@SessionScoped
public class BeanUsuarios implements Serializable {
	private static final long serialVersionUID = 55555L;
	// Se añade este atributo de entidad para recibir el usuario concreto
	// selecionado de la tabla o de un formulario
	// Es necesario inicializarlo para que al entrar desde el formulario de
	// AltaForm.xml se puedan
	// dejar los avalores en un objeto existente.

	// uso de inyección de dependencia
	@ManagedProperty(value = "#{usuario}")
	private BeanUsuario usuario;

	private User user = null;
	
	private String password;
	
	private String passwordConfirmacion;
	
	private User[] usuarios = null;

	public BeanUsuario getUsuario() {
		return usuario;
	}

	public void setUsuario(BeanUsuario usuario) {
		this.usuario = usuario;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	public String getPasswordConfirmacion() {
		return passwordConfirmacion;
	}

	public void setPasswordConfirmacion(String passwordConfirmacion) {
		this.passwordConfirmacion = passwordConfirmacion;
	}
	
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public User[] getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(User[] usuarios) {
		this.usuarios = usuarios;
	}

	// Se inicia correctamente el MBean inyectado si JSF lo hubiera crea
	// y en caso contrario se crea. (hay que tener en cuenta que es un Bean de
	// sesión)
	// Se usa @PostConstruct, ya que en el contructor no se sabe todavía si el
	// Managed Bean
	// ya estaba construido y en @PostConstruct SI.
	@PostConstruct
	public void init() {
		System.out.println("BeanUsuarios - PostConstruct");
		// Buscamos el usuario en la sesión. Esto es un patrón factoría
		// claramente.
		usuario = (BeanUsuario) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("usuario"));
		// si no existe lo creamos e inicializamos
		if (usuario == null) {
			System.out.println("BeanUsuarios - No existia");
			usuario = new BeanUsuario();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("usuario", usuario);
		}
	}

	@PreDestroy
	public void end() {
		System.out.println("BeanUsuarios - PreDestroy");
	}

	public void iniciaUsuario(ActionEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// Obtenemos el archivo de propiedades correspondiente al idioma que
		// tengamos seleccionado y que viene envuelto en facesContext
		ResourceBundle bundle = facesContext.getApplication()
				.getResourceBundle(facesContext, "msgs");
		usuario.setId(null);
		usuario.setLogin(bundle.getString("valorDefectoUserId"));
		usuario.setEmail(bundle.getString("valorDefectoCorreo"));
	}

	public String login() {
		UserService us = Factories.getUserService();
		User userByLogin = null;
		
		try {
			userByLogin = us.findLoggableUser(usuario.getLogin(), usuario.getPassword());
		} catch (BusinessException e) {
			System.out.println(e.getMessage());
		}
		if(userByLogin == null){
			return "error";
		}else{
			FacesContext.getCurrentInstance().getExternalContext()
			.getSessionMap().put("usuario", userByLogin);
			user = userByLogin;
			System.out.println(user.getLogin());
			if(user.getIsAdmin()){
				return "admin";
			}
			return "user";
		}
	}
	
	public String registrar(){
		User uregistro = user;
		user = new User();
		if(uregistro.getPassword().equals(passwordConfirmacion)){
			UserService userService = Factories.getUserService();
			try {
				userService.registerUser(uregistro);
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
				return "error";
			}
			return "exito";
		}
		return "error";
	}
	
	public void modificar(){
		if(password.equals(passwordConfirmacion)){
			UserService us = Factories.getUserService();
			try {
				if(password!=null && passwordConfirmacion!=null){
					user.setPassword(password);
				}
				us.updateUserDetails(user);
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		password="";
		passwordConfirmacion="";
	}
	
	public String listarUsuarios(){
		AdminService as = Factories.getAdminService();
		try {
			usuarios = (User[]) as.findAllUsers().toArray(new User[0]);
			return "exito";
		} catch (BusinessException e) {
			System.out.println(e.getMessage());
		}
		return "error";
	}
	
	public String cambioVista(){
		return "exito";
	}
}
