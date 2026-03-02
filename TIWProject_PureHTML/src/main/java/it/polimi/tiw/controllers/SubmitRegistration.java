package it.polimi.tiw.controllers;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.EmailValidator;

/**
 * Servlet implementation class SubmitRegistration
 */
@WebServlet("/SubmitRegistration")
public class SubmitRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SubmitRegistration() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
    	
    	ServletContext context = getServletContext();
    	connection = ConnectionHandler.getConnection(context);
    	ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(context);
    	templateResolver.setTemplateMode(TemplateMode.HTML);
    	this.templateEngine = new TemplateEngine();
    	this.templateEngine.setTemplateResolver(templateResolver);
    	templateResolver.setSuffix(".html");

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String usrn = request.getParameter("username");
		String mail = request.getParameter("mail");
		String pwd = request.getParameter("pwd");
		String chkpwd = request.getParameter("checkpwd");
		String msg = null;
		
		UserDAO usr = new UserDAO(connection);
		boolean uniqueUsername = false;
		try {
			uniqueUsername = usr.checkUniqueUsername(usrn);
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in database credential checking."/* + e.getMessage()*/);
		}
		
		if(usrn.isBlank() && !uniqueUsername)
			msg = "Username invalid or already taken";
		
		if((!chkpwd.equals(pwd) || pwd.isBlank()) && msg == null) 
			msg = "Password not repeated correctly or invalid(blank)";
		
		if(!EmailValidator.isValidEmail(mail) && msg == null)
			msg = "Invalid mail";
		
		if(msg == null) 
			try {
				usr.createUser(usrn, pwd, mail);
				msg = "Registration Succeeded! Now login with your credentials";
			} catch (SQLException e) {
				msg = "";
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in new user creation."/* + e.getMessage()*/);
			}
		
		
		String path = "/index.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("errRegMsg", msg);
		templateEngine.process(path, ctx, response.getWriter());	

	}
	

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
