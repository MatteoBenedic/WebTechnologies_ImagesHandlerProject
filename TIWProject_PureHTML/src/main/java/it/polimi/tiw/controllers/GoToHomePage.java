package it.polimi.tiw.controllers;

import java.io.IOException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.UserDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class GoToHomePage
 */
@WebServlet("/Home")
public class GoToHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	private TemplateEngine templateEngine = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GoToHomePage() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    public void init() throws ServletException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext context = getServletContext();
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
		int choice;
		int err;
		try {
			choice = Integer.parseInt(request.getParameter("uplmsg"));
		}catch(NumberFormatException | NullPointerException e) {
			choice = 5;
		}
		
		try {
			err = Integer.parseInt(request.getParameter("err"));
		}catch(NumberFormatException | NullPointerException e) {
			err = 0;
		}
		
		String path = "/WEB-INF/homepage.html";
		AlbumDAO albDao = new AlbumDAO(connection);
		List<Album> myAlbums = new ArrayList<>();
		List<Album> otherAlbums = new ArrayList<>();
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		User user = (User) session.getAttribute("user");
		try {
			myAlbums = albDao.findAlbumByUser(user.getUsername());
			otherAlbums = albDao.findAlbumByOthers(user.getUsername());
			
			if(myAlbums == null) 
				myAlbums = new ArrayList<Album>();
			
			
			if(otherAlbums == null) 
				otherAlbums = new ArrayList<Album>();
			
		}catch(SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failure in extracting albums."/* + e.getMessage()*/);
		}
		
		String errMess;
		String imgMsg;
		switch(choice) {
		case 0:
			imgMsg = "File Error";
			break;
		case 1:
			imgMsg = "File not an Image";
			break;
		case 2:
			imgMsg = "Title invalid";
			break;
		case 3: 
			imgMsg = "Description invalid";
			break;
		case 4:
			imgMsg = "Upload Succeded";
			break;
		case 5: 
			imgMsg = "";
			break;
		default:
			imgMsg = "";
			break;
		}
		
		/*if(err == 1)
			errMess = "Error from invalid request";
		else
			errMess = "";*/
		
		switch(err) {
		case 0:
			errMess = "";
			break;
		case 1: 
			errMess = "Error from invalid request";
			break;
		case 2: 
			errMess = "Error from trying access unauthorized content";
			break;
		case 3:
			errMess = "Invalid redirecting after completing task led to home";
			break;
		case 4:
			errMess = "No images in this album (or non existing album)";
			break;
		default:
			errMess = "";
			break;
		}
		
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("myAlbums", myAlbums);
		ctx.setVariable("otherAlbums", otherAlbums);
		ctx.setVariable("errUploadMsg", imgMsg);
		ctx.setVariable("errMess", errMess);
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
