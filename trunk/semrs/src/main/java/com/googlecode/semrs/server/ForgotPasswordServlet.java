package com.googlecode.semrs.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.googlecode.semrs.model.User;
import com.googlecode.semrs.server.service.GenericService;
import com.googlecode.semrs.util.Util;

public class ForgotPasswordServlet extends HttpServlet {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private GenericService genericService;
	
    private com.googlecode.semrs.server.NewPasswordNotifier newPasswordNotifier;
	
	private String randomPassword;
	
	private static final Log LOG = LogFactory.getLog(ForgotPasswordServlet.class);
	
	public void init(ServletConfig config) throws ServletException {

		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(config.getServletContext());
		genericService = (GenericService) wac.getBean("genericService");
		newPasswordNotifier = (com.googlecode.semrs.server.NewPasswordNotifier) wac.getBean("newPasswordNotifier");
	}
	
	  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      	handleRequest(req, resp);
      }
	  
	  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
      	handleRequest(req, resp);
      }
	  
	  private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		  
		  
		  String username = req.getParameter("username");
		  String email    = req.getParameter("email");
		  HttpSession httpSession = req.getSession();
		  try{
		  
		  httpSession.removeAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
			  
		  if(genericService.exists(User.class, username)){
			  
			 User user = (User) genericService.load(User.class, username, true);
			 if(user.getEmail().equals(email)){
				 randomPassword = Util.getRandomPassword(8);
				 newPasswordNotifier.notifyNewPassword(user,randomPassword);
			     user.setPassword(Util.hashString(randomPassword));			     
			     genericService.save(user);
			     httpSession.setAttribute("forgotpassword", "success");
			     resp.sendRedirect("/semrs/login.jsp");
			 }else{
				 httpSession.setAttribute("error", "El correo suministrado no existe en nuestra base de datos.");
				 resp.sendRedirect("/semrs/forgotPassword.jsp");
			 }
			    
		  }else{
			  httpSession.setAttribute("error", "El Usuario suministrado no existe.");
			  resp.sendRedirect("/semrs/forgotPassword.jsp");

		  }
		  
		  }catch(Exception e){
		      LOG.error("Error sending email: " + e.toString());
			  httpSession.setAttribute("forgotpassword", "fail");
			  resp.sendRedirect("/semrs/login.jsp");
			  
		  }
		  

	     
	 }
	  

	

}
