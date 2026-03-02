package it.polimi.tiw.controllers;

import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
import javax.servlet.http.Part;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.dao.AlbumDAO;
import it.polimi.tiw.dao.ImageDAO;
import it.polimi.tiw.utils.ConnectionHandler;

/**
 * Servlet implementation class UploadImage
 */
@WebServlet("/UploadImage")
@MultipartConfig
public class UploadImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private Connection connection = null;
    
    String folderPath = "";
    
    public void init() throws ServletException {
		folderPath = getServletContext().getInitParameter("folderpath");
		
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContext context = getServletContext();
	}
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadImage() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Part filePart  = null;
		String title = "";
		String description = "";
		String fileName = "";
		String[] albums = null;
		
		
		try {
		filePart = request.getPart("file"); 
		title = request.getParameter("title");
		description = request.getParameter("description");
		fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		albums = request.getParameterValues("albums");
		}catch(Exception e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid Request");
			return;

		}
		
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("You're not logged");
			return;
		}
		String username = ((User) session.getAttribute("user")).getUsername();
		List<Album> myAlbums = new ArrayList<>();
		AlbumDAO albDao = new AlbumDAO(connection);
		try {
			myAlbums = albDao.findAlbumByUser(username);
		
			if(myAlbums == null) 
				myAlbums = new ArrayList<Album>();
			
		}catch(SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Failure in extracting albums from database");
			return;
		}
		
		List<Integer> albumIds = new ArrayList<>();
		int val;
		boolean mine = false;
		for(String a : albums) {
			val = Integer.parseInt(a);
			for(int i = 0; i < myAlbums.size()  && !mine; i++) {
				if(val == myAlbums.get(i).getId())
					mine = true;
			}
			if(mine)
				albumIds.add(val);
		}
		
		if(albumIds.size() < 1) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid Request");
			return;
		}
			
		
		
		
		String msg = null;
		if(filePart == null || filePart.getSize() <= 0) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Expected a file");
			return;

		}
		
		String contentType = filePart.getContentType();
		if (!contentType.startsWith("image")) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Expected an image file");
			return;

		}
		
		if(title.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid title");
			return;

		}
		
		if(description.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Invalid description");
			return;

		}
			
		ImageDAO imgDAO = new ImageDAO(connection);
		int imgId = 0;
		try {
			String path = getServletContext().getInitParameter("GetImgPath");
			imgId = imgDAO.createImage(username, title, description, albumIds, path, fileName);
			
			String outputPath = folderPath + imgId + fileName; 
			File file = new File(outputPath);
			try (InputStream fileContent = filePart.getInputStream()) {
				Files.copy(fileContent, file.toPath());
					
			} catch (Exception e) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter().println("Failure in saving file");
				return;
			}
		}catch(SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Failure in saving file in the database");
			return;
		}

		
		response.setStatus(HttpServletResponse.SC_OK);
		response.getWriter().println("Image uploaded successfully");
	}
	
	
	public void destroy() {
		try {
			ConnectionHandler.closeConnection(connection);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
