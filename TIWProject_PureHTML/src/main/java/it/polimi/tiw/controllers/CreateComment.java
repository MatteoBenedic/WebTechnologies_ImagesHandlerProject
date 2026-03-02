package it.polimi.tiw.controllers;

import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.context.WebContext;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.CommentDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class CreateComment
 */
@WebServlet("/CreateComment")
public class CreateComment extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CreateComment() {
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
		String ctxpath = getServletContext().getContextPath();
		String errpath = ctxpath + "/Home";
		
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			String loginpath = getServletContext().getContextPath() + "/index.html";
			response.sendRedirect(loginpath);
			return;
		}
		
		int id = -1;
		int aId = -1;
		boolean isBadRequest;
		
		try {
			id = Integer.parseInt(request.getParameter("id"));
		}catch(NumberFormatException | NullPointerException e){
			isBadRequest = true;
			e.printStackTrace();
		}
		
		String commt = "";
		try {
			commt = request.getParameter("comment");
			
			isBadRequest = commt.isBlank();
		} catch (NullPointerException e) {
			isBadRequest = true;
			e.printStackTrace();
		}
		if (isBadRequest) {
			response.sendRedirect(errpath + "?err=" + 1);
			return;
		}

		String user = ((User) session.getAttribute("user")).getUsername();
		CommentDAO cmtDAO = new CommentDAO(connection);
		try {
			cmtDAO.createComment(commt, user, id);
		} catch (SQLException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Not possible to create album");
			return;
		}
		
		try {
			aId = Integer.parseInt(request.getParameter("albId"));
		}catch(NumberFormatException | NullPointerException e) {
			aId = -1;
		}
		
		String path = ctxpath + "/GoToImagePage?imgId=" + id + "&albId=" + aId;
		response.sendRedirect(path);
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
