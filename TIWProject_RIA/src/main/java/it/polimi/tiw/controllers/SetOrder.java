package it.polimi.tiw.controllers;

import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.ConnectionHandler;
import it.polimi.tiw.utils.RequestPayload;

/**
 * Servlet implementation class SetOrder
 */
@WebServlet("/SetOrder")
@MultipartConfig
public class SetOrder extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Connection connection = null;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SetOrder() {
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
		RequestPayload payload = new RequestPayload();
		try {
			Gson gson = new Gson();
			payload = gson.fromJson(new InputStreamReader(request.getInputStream()), RequestPayload.class);
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Can't extract data from request");
		}
		
		List<Album> myAlbums = new ArrayList<>();
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("You're not logged");
			return;
		}
		AlbumDAO albDao = new AlbumDAO(connection);
		User user = (User) session.getAttribute("user");
		try {
			myAlbums = albDao.findAlbumByUser(user.getUsername());
		
			if(myAlbums == null) 
				myAlbums = new ArrayList<Album>();
			
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Failure in extracting albums from database");
			return;
		}
		
		boolean mine = false;
		for(Album a: myAlbums) {
			if (a.getCreator().equals(user.getUsername())) {
				mine = true;
				break;
			}
		}
		
		if(mine) {
			ImageDAO imgDAO = new ImageDAO(connection);
			try {
				imgDAO.setOrder(payload.getAlbumId(), payload.getIds());
			}catch(SQLException e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().print("Impossible to set order");
			}
		}else {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("Not your album");
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
        
	}

}
