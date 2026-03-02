package it.polimi.tiw.controllers;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.EmailValidator;

/**
 * Servlet implementation class SubmitRegistration
 */
@WebServlet("/SubmitRegistration")
@MultipartConfig
public class SubmitRegistration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
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
		String usrn = request.getParameter("usrn");
		String mail = request.getParameter("mail");
		String pwd = request.getParameter("passwd");
		String chkpwd = request.getParameter("checkpwd");
		String msg = null;
		
		UserDAO usr = new UserDAO(connection);
		boolean uniqueUsername = false;
		try {
			uniqueUsername = usr.checkUniqueUsername(usrn);
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Failure in database credential checking.");
			return;
		}
		
		if(usrn.isBlank() && !uniqueUsername) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Username invalid or already taken");
			return;
		}
		
		
		if((!chkpwd.equals(pwd) || pwd.isBlank())) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Password not repeated correctly or invalid(blank)");
			return;
		}
		
		if(!EmailValidator.isValidEmail(mail)) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid mail");
			return;
		}

		

		try {
				usr.createUser(usrn, pwd, mail);
				response.setStatus(HttpServletResponse.SC_OK);
				response.getWriter().println("Registration Succeeded! Now login with your credentials");
		} catch (SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Failure in new user creation.");
		}
		
		
		
		

	}
	

	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
