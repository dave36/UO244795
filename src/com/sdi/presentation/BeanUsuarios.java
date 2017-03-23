package com.sdi.presentation;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.application.FacesMessage;
import javax.faces.bean.*;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpSession;

import alb.util.log.Log;

import com.sdi.business.AdminService;
import com.sdi.business.TaskService;
import com.sdi.business.UserService;
import com.sdi.business.exception.BusinessException;
import com.sdi.dto.Task;
import com.sdi.dto.User;
import com.sdi.dto.types.UserStatus;
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

	private User user = new User();
	
	@ManagedProperty(value = "#{tarea}")
	private BeanTarea tarea;
	
	private Task task = new Task();
	
	private User seleccionado = new User();
	
	private String password;
	
	private String passwordConfirmacion;
	
	private List<User> usuarios = null;
	
	private List<Task> tareas = null;
	
	private Task seleccionada = new Task();
	
	private boolean inbox = false;
	
	private boolean hoy = false;
	
	private boolean semana = false;
	
	private boolean mostrarTerminadas = false;

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
	
	public Task getTask() {
		return task;
	}

	public void setTask(Task task) {
		this.task = task;
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
	
	public List<User> getUsuarios() {
		return usuarios;
	}

	public void setUsuarios(List<User> usuarios) {
		this.usuarios = usuarios;
	}
	
	public User getSeleccionado(){
		return seleccionado;
	}
	
	public void setSeleccionado(User user){
		this.seleccionado = user;
	}
	
	public List<Task> getTareas() {
		return tareas;
	}

	public void setTareas(List<Task> unfinished) {
		this.tareas = unfinished;
	}

	public boolean isInbox() {
		return inbox;
	}

	public void setInbox(boolean inbox) {
		this.inbox = inbox;
	}

	public boolean isHoy() {
		return hoy;
	}

	public void setHoy(boolean hoy) {
		this.hoy = hoy;
	}

	public boolean isSemana() {
		return semana;
	}

	public void setSemana(boolean semana) {
		this.semana = semana;
	}

	public boolean getMostrarTerminadas() {
		return mostrarTerminadas;
	}

	public void setMostrarTerminadas(boolean mostrarTerminadas) {
		this.mostrarTerminadas = mostrarTerminadas;
	}

	public Task getSeleccionada() {
		return seleccionada;
	}

	public void setSeleccionada(Task seleccionada) {
		this.seleccionada = seleccionada;
	}
	
	public Date getDate(){
		return new Date();
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
		
		tarea = (BeanTarea) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("tarea"));
		if (tarea == null) {
			System.out.println("BeanTarea - No existia");
			tarea = new BeanTarea();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("tarea", tarea);
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
			FacesContext.getCurrentInstance().addMessage("form-login", 
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Usuario o clave no valida",
							"Error en el login"));
			
			return "error";
		}else{
			FacesContext.getCurrentInstance().getExternalContext()
			.getSessionMap().put("LOGIN_USER", userByLogin);
			user = userByLogin;
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
		else{
			FacesContext.getCurrentInstance().addMessage("form-registro", 
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							"Las contraseñas deben coincidir",
							"Error en el login"));
			return "error";
		}
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
	
	public void inicializarBD(){
		AdminService as = Factories.getAdminService();
		try {
			as.dropAndInsert();
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"Se han cargado los usuarios correctamente",
							"Cargar usuarios"));
		} catch (BusinessException e) {
			e.printStackTrace();
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Error al cargar usuarios",
							"Error al cargar usuarios"));
		}
	}
	
	public String listarUsuarios(){
		AdminService as = Factories.getAdminService();
		try {
			usuarios = as.findAllUsers();
			return "exito";
		} catch (BusinessException e) {
			System.out.println(e.getMessage());
		}
		return "error";
	}
	
	public void modificarStatus(){
		if(!seleccionado.getIsAdmin()){
			AdminService as = Factories.getAdminService();
			if(seleccionado.getStatus().equals(UserStatus.ENABLED)){
				try {
					as.disableUser(seleccionado.getId());
				} catch (BusinessException e) {
					System.out.println(e.getMessage());
				}
			}
			else{
				try {
					as.enableUser(seleccionado.getId());
				} catch (BusinessException e) {
					System.out.println(e.getMessage());
				}
			}
			try {
				usuarios = as.findAllUsers();
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void eliminarUsuario(){
		System.out.println(seleccionado.getLogin());
		if(!seleccionado.getIsAdmin()){
			AdminService as = Factories.getAdminService();
			try {
				as.deepDeleteUser(seleccionado.getId());
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
			try {
				usuarios = as.findAllUsers();
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	public void cargarTodas(){
		TaskService ts = Factories.getTaskService();
		try {
			if(!mostrarTerminadas){
				tareas = ts.findInboxTasksByUserId(user.getId());
			}
			else{
				tareas = ts.findTasksByUserId(user.getId());
			}			
		} catch (BusinessException e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public String cargarTareas(){
		TaskService ts = Factories.getTaskService();
		if(inbox){
			try {
				tareas = ts.findInboxTasksByUserId(user.getId());
				return "inbox";
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		else if(hoy){
			try {
				tareas = ts.findTodayTasksByUserId(user.getId());
				return "hoy";
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		else if(semana){
			try {
				tareas = ts.findWeekTasksByUserId(user.getId());
				return "semana";
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		return "error";
	}
	
	public void finalizarTarea(){
		TaskService ts = Factories.getTaskService();
		try {
			ts.markTaskAsFinished(seleccionada.getId());
			cargarTareas();
		} catch (BusinessException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public String añadirTarea() {
		return "tarea";
	}
	
	public String crearTarea() {
		Task tarea = task;
		task = new Task();
		TaskService ts = Factories.getTaskService();
		if(inbox){
			try {
				tarea.setUserId(user.getId());
				ts.createTask(tarea);
				return "inbox";
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		if(hoy){
			try {
				tarea.setUserId(user.getId());
				ts.createTask(tarea);
				cargarTareas();
				return "hoy";
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		if(semana){
			try {
				tarea.setUserId(user.getId());
				ts.createTask(tarea);
				return "semana";
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		return "error";
	}
	
	public String edicionDeTarea() {
		if (seleccionada != null)
			return "editar";
		return "error";
	}
	
	public String editarTarea() {
		TaskService ts = Factories.getTaskService();
		if(inbox){
			try {
				ts.updateTask(seleccionada);
				cargarTareas();
				return "inbox";
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		else if(hoy){
			try {
				ts.updateTask(seleccionada);
				cargarTareas();
				return "hoy";
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		else if(semana){
			try {
				ts.updateTask(seleccionada);
				cargarTareas();
				return "semana";
			} catch (BusinessException e) {
				System.out.println(e.getMessage());
			}
		}
		return "error";
	}
	
	public String cerrarSesion(){
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().
											getExternalContext().
											getSession(false);
		session.invalidate();
		return "cerrar";
	}
	
	//------ Comprobaciones para color
	
	public boolean retrasada(Task task){
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		String format = df.format(task.getPlanned());
		String ahora = df.format(new Date());
		try {
			return (df.parse(ahora).after(df.parse(format)));
		} catch (ParseException e) {
			Log.warn(e.getMessage());
		}
		return false;
	}
	
	public boolean finalizada(Task task){
		return task.getFinished() != null;
	}
	
	//------ Validadores
	
	public void loginValidator(FacesContext context, UIComponent component,
			Object value) throws ValidatorException{
		String userlogin = "";
		if(value!=null){
			userlogin = value.toString();
		}
		UserService us = Factories.getUserService();
		try {
			User login = us.findRepeatUser(userlogin);
			if(login!=null){
				FacesMessage message = new FacesMessage("Login repetido");
				throw new ValidatorException(message);
			}
		} catch (BusinessException e) {
			e.printStackTrace();
		}
		
	}
	
	public void passwValidator(FacesContext context, UIComponent component,
			Object value) throws ValidatorException{
		String password = "";
		if(value!=null){
			password = value.toString();
		}
		boolean letras = false;
		boolean numeros = false;
		
        for(int i=0; i<password.length(); i++){
        	
        	if(Character.isLetter(password.charAt(i))){
        		letras = true;
        	}
        	if(Character.isDigit(password.charAt(i))){
        		numeros=true;
        	}
        }
        if(!(numeros&&letras) || password.length() < 8){
        	FacesMessage message = new FacesMessage("Contraseña incorrecta");
        	throw new ValidatorException(message);
        }
	}
}
