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
import com.sdi.dto.Category;
import com.sdi.dto.Task;
import com.sdi.dto.User;
import com.sdi.dto.types.UserStatus;
import com.sdi.infrastructure.Factories;

@ManagedBean(name = "controller")
@SessionScoped
public class BeanUsuarios implements Serializable {
	
	private static final long serialVersionUID = 55555L;
	// uso de inyección de dependencia
	@ManagedProperty(value = "#{usuario}")
	private BeanUsuario usuario;
	
	@ManagedProperty(value = "#{tarea}")
	private BeanTarea tarea;
	
	private User user = new User();
	
	private User seleccionado = new User();
	
	private Task task = new Task();
	
	private Task seleccionada = new Task();
	
	private List<User> usuarios = null;
	
	private List<Task> tareas = null;
	
	private List<Category> categorias = null;
	
	private String password;
	
	private String passwordConfirmacion;
	
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
	
	public List<Category> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Category> categorias) {
		this.categorias = categorias;
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
		Log.info("BeanUsuarios - PostConstruct");
		// Buscamos el usuario en la sesión. Esto es un patrón factoría
		// claramente.
		usuario = (BeanUsuario) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("usuario"));
		// si no existe lo creamos e inicializamos
		if (usuario == null) {
			Log.info("BeanUsuarios - No existia - Se ha creado una instancia");
			usuario = new BeanUsuario();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("usuario", usuario);
		}
		
		tarea = (BeanTarea) FacesContext.getCurrentInstance()
				.getExternalContext().getSessionMap().get(new String("tarea"));
		if (tarea == null) {
			tarea = new BeanTarea();
			FacesContext.getCurrentInstance().getExternalContext()
					.getSessionMap().put("tarea", tarea);
		}
	}

	@PreDestroy
	public void end() {
		Log.info("BeanUsuarios - PreDestroy");
	}

	public void iniciaUsuario(ActionEvent event) {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		// Obtenemos el archivo de propiedades correspondiente al idioma que
		// tengamos seleccionado y que viene envuelto en facesContext
		ResourceBundle bundle = facesContext.getApplication()
				.getResourceBundle(facesContext, "msgs");
		usuario.setId(null);
		usuario.setLogin(bundle.getString(null));
		usuario.setEmail(bundle.getString(null));
	}

	public String login() {
		Log.info("Intento de login del usuario " + usuario.getLogin());
		UserService us = Factories.getUserService();
		User userByLogin = null;
		
		try {
			userByLogin = us.findLoggableUser(usuario.getLogin(), usuario.getPassword());
		} catch (BusinessException e) {
			Log.warn(e.getMessage());
		}
		if(userByLogin == null){
			FacesContext.getCurrentInstance().addMessage("form-login", 
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							getBundle().getString("noUserPass"),
							"Error en el login"));
			usuario.setLogin("");
			Log.warn("El usuario " + usuario.getLogin() + " no está en el sistema");
			return "error";
		}else{
			FacesContext.getCurrentInstance().getExternalContext()
			.getSessionMap().put("LOGIN_USER", userByLogin);
			user = userByLogin;
			Log.info("El usuario " + user.getLogin() + " ha iniciado sesión");
			if(user.getIsAdmin()){
				return "admin";
			}
			return "user";
		}
	}
	
	public String registrar(){
		Log.info("Intento de registro en la aplicación");
		User uregistro = user;
		user = new User();
		if(uregistro.getPassword().equals(passwordConfirmacion)){
			UserService userService = Factories.getUserService();
			try {
				userService.registerUser(uregistro);
			} catch (BusinessException e) {
				Log.warn(e.getMessage());
				return "error";
			}
			Log.info("El usuario " + uregistro.getLogin() + " se ha registrado"
					+ " en el sistema");
			return "exito";
		}
		else{
			FacesContext.getCurrentInstance().addMessage("form-registro", 
					new FacesMessage(FacesMessage.SEVERITY_WARN,
							getBundle().getString("igualPass"),
							"Error en el login"));
			Log.warn("El anónimo " + uregistro.getLogin() + " se ha intentado"
					+ " registrar con contraseñas diferentes");
			return "error";
		}
	}
	
	public void inicializarBD(){
		AdminService as = Factories.getAdminService();
		try {
			as.dropAndInsert();
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							getBundle().getString("userBienCargados"),
							"Cargar usuarios"));
			Log.info("El administrador " + user.getLogin() + " ha reiniciado la"
					+ " base de datos de forma correcta");
		} catch (BusinessException e) {
			Log.warn(e.getMessage());
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							getBundle().getString("userMalCargados"),
							"Error al cargar usuarios"));
		}
	}
	
	public String listarUsuarios(){
		Log.info("El administrador " + user.getLogin() + " solicita listar los "
				+ "usuarios del sistema");
		AdminService as = Factories.getAdminService();
		try {
			usuarios = as.findAllUsers();
			Log.info("Usuarios correctamente listados");
			return "exito";
		} catch (BusinessException e) {
			Log.warn(e.getMessage());
		}
		return "error";
	}
	
	public void modificarStatus(){
		Log.info("El administrador " + user.getLogin() + "solicita cambiar el "
				+ "status del usuario " + seleccionado.getLogin());
		if(!seleccionado.getIsAdmin()){
			AdminService as = Factories.getAdminService();
			if(seleccionado.getStatus().equals(UserStatus.ENABLED)){
				try {
					as.disableUser(seleccionado.getId());
				} catch (BusinessException e) {
					Log.warn(e.getMessage());
				}
			}
			else{
				try {
					as.enableUser(seleccionado.getId());
				} catch (BusinessException e) {
					Log.warn(e.getMessage());
				}
			}
			Log.info("Cambiado el status a " + seleccionado.getStatus() + 
					" de forma correcta");
			try {
				usuarios = as.findAllUsers();
				Log.info("Se recarga la lista de usuarios correctamente");
			} catch (BusinessException e) {
				Log.warn(e.getMessage());
			}
		}
	}
	
	public void eliminarUsuario(){
		Log.info("El administrador " + user.getLogin() + " solicita eliminar al"
				+ " usuario " + seleccionado.getLogin());
		if(!seleccionado.getIsAdmin()){
			AdminService as = Factories.getAdminService();
			try {
				as.deepDeleteUser(seleccionado.getId());
				Log.info("El usuario " + seleccionado.getLogin() + " ha sido "
						+ "eliminado correctamente");
			} catch (BusinessException e) {
				Log.warn(e.getMessage());
			}
			try {
				usuarios = as.findAllUsers();
				Log.info("Se recarga la lista de usuarios correctamente");
			} catch (BusinessException e) {
				Log.warn(e.getMessage());
			}
		}
	}
	
	public String volverAListas(){
		inbox = hoy = semana = mostrarTerminadas = false;
		return "listas";
	}
	
	public void cargarTodas(){
		Log.info("El usuario " + user.getLogin() + " solicita listar las tareas"
				+ " de Inbox");
		TaskService ts = Factories.getTaskService();
		try {
			if(!mostrarTerminadas){
				tareas = ts.findInboxTasksByUserId(user.getId());
				Log.info("Tareas de Inbox listadas correctamente");
			}
			else{
				tareas = ts.findTasksByUserId(user.getId());
				Log.info("Tareas de Inbox mas finalizadas listadas correctamente");
			}			
		} catch (BusinessException e) {
			Log.warn(e.getMessage());
		}
	}
	
	
	public String cargarTareas(){
		Log.info("El usuario " + user.getLogin() + " solicita listar tareas");
		if(inbox&&hoy || inbox&&semana || semana&&hoy){
			FacesContext.getCurrentInstance().addMessage("form-usuario", 
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							getBundle().getString("seleccionLista"),
							"Error al cargar usuarios"));
			inbox = hoy = semana = false;
			Log.warn("El usuario " + user.getLogin() + " ha intentado listar mas"
					+ " de una lista de tareas a la vez");
			return "";
		}
		else{
			cargarCategorias();
			TaskService ts = Factories.getTaskService();
			if(inbox){
				try {
					tareas = ts.findInboxTasksByUserId(user.getId());
					Log.info("Tareas de Inbox listadas correctamente");
					return "inbox";
				} catch (BusinessException e) {
					Log.warn(e.getMessage());
				}
			}
			else if(hoy){
				try {
					tareas = ts.findTodayTasksByUserId(user.getId());
					Log.info("Tareas de Hoy listadas correctamente");
					return "hoy";
				} catch (BusinessException e) {
					Log.warn(e.getMessage());
				}
			}
			else if(semana){
				try {
					tareas = ts.findWeekTasksByUserId(user.getId());
					Log.info("Tareas de Semana listadas correctamente");
					return "semana";
				} catch (BusinessException e) {
					Log.warn(e.getMessage());
				}
			}
			return "error";
		}
	}
	
	public void finalizarTarea(){
		Log.info("El usuario " + user.getLogin() + " solicita finalizar la tarea "
				+ seleccionada.getTitle());
		TaskService ts = Factories.getTaskService();
		try {
			ts.markTaskAsFinished(seleccionada.getId());
			cargarTareas();
			Log.info("Tarea eliminada correctamente y lista de tareas recargada");
		} catch (BusinessException e) {
			Log.warn(e.getMessage());
		}
	}
	
	public String añadirTarea() {
		Log.info("El usuario " + user.getLogin() + " solicita crear una nueva "
				+ "tarea");
		cargarCategorias();
		return "tarea";
	}
	
	public String crearTarea(){
		Task tarea = task;
		task = new Task();
		TaskService ts = Factories.getTaskService();
		tarea.setUserId(user.getId());
		try {
			ts.createTask(tarea);
			Log.info("Tarea " + tarea.getTitle() + " creada correctamente");
			return "exito";
		} catch (BusinessException e) {
			Log.warn(e.getMessage());
			return "error";
		}
	}
	
	public String edicionDeTarea() {
		Log.info("El usuario " + user.getLogin() + " solicita editar la tarea "
				+ seleccionada.getTitle());
		if (seleccionada != null){
			cargarCategorias();
			return "editar";
		}
		Log.warn("No se ha seleccionado tarea en la lista");
		return "error";
	}
	
	public String editarTarea() {
		TaskService ts = Factories.getTaskService();
		if(inbox){
			try {
				ts.updateTask(seleccionada);
				cargarTodas();
				Log.info("La tarea " + seleccionada.getTitle() + " ha sido "
						+ "editada correctamente y la lista Inbox se ha recargado");
				return "inbox";
			} catch (BusinessException e) {
				Log.warn(e.getMessage());
			}
		}
		else if(hoy){
			try {
				ts.updateTask(seleccionada);
				cargarTareas();
				Log.info("La tarea " + seleccionada.getTitle() + " ha sido "
						+ "editada correctamente y la lista Hoy se ha recargado");
				return "hoy";
			} catch (BusinessException e) {
				Log.warn(e.getMessage());
			}
		}
		else if(semana){
			try {
				ts.updateTask(seleccionada);
				cargarTareas();
				Log.info("La tarea " + seleccionada.getTitle() + " ha sido "
						+ "editada correctamente y la lista Semana se ha recargado");
				return "semana";
			} catch (BusinessException e) {
				Log.warn(e.getMessage());
			}
		}
		return "error";
	}
	
	public void cargarCategorias(){
		TaskService ts = Factories.getTaskService();
		try {
			categorias = ts.findCategoriesByUserId(user.getId());
			Log.info("Se han cargado las categorias del usuario " 
					+ user.getLogin());
		} catch (BusinessException e) {
			Log.warn(e.getMessage());
		}
	}
	
	public String cerrarSesion(){
		Log.info("El usuario " + user.getLogin() + " solicita cerrar sesión");
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance().
											getExternalContext().
											getSession(false);
		session.invalidate();
		Log.info("Sesión cerrada correctamente");
		return "cerrar";
	}
	
	//------ Bundle
	
	private ResourceBundle getBundle(){
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ResourceBundle bundle = 
		facesContext.getApplication().getResourceBundle(facesContext, "msgs");
		return bundle;
	}
	
	//------ Comprobaciones para color
	
	public boolean retrasada(Task task){
		if(task.getPlanned()!=null){
			SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			String format = df.format(task.getPlanned());
			String ahora = df.format(new Date());
			try {
				return (df.parse(ahora).after(df.parse(format)));
			} catch (ParseException e) {
				Log.warn(e.getMessage());
			}
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
				FacesMessage message = new FacesMessage(
						getBundle().getString("loginRep"));
				throw new ValidatorException(message);
			}
		} catch (BusinessException e) {
			Log.warn(e.getMessage());
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
        	FacesMessage message = new FacesMessage(
        			getBundle().getString("malPass"));
        	throw new ValidatorException(message);
        }
	}
}