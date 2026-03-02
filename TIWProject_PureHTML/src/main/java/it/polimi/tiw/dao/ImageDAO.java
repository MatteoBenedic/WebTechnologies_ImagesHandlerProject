package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Album;
import it.polimi.tiw.beans.Image;

public class ImageDAO {
	private Connection con;

	String folderPath = "";
	public ImageDAO(Connection connection) {
		this.con = connection;
	}
	
	public int createImage(String username, String title, String description, List<Integer> albums, String pathFolder, String fileName) throws SQLException{
		con.setAutoCommit(false);
		java.util.Date utilTime = new java.util.Date();
		java.sql.Timestamp sqlTime = new java.sql.Timestamp(utilTime.getTime());
		
		String filepath = "";
		
		String query1 = "INSERT INTO image(title, description, filepath, owner, creation_date) VALUES(?, ?, ?, ?, ?)";
		PreparedStatement pstatement1 = null;
		ResultSet keys = null;
		
		String query2 = "UPDATE image SET filepath = ? WHERE imageId = ?";
		PreparedStatement pstatement2 = null;
		
		String[] query3 = new String[albums.size()];
		PreparedStatement[] pstatement3 = new PreparedStatement[albums.size()];
		for(int i = 0; i<albums.size(); i++) {
			query3[i] = "INSERT INTO catalog(albId, imgId) VALUES(? , ?)";
			pstatement3[i] = null;
		}
	
		try {
			pstatement1 = con.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
			pstatement1.setString(1, title);
			pstatement1.setString(2, description);
			pstatement1.setString(3, filepath);
			pstatement1.setString(4, username);
			pstatement1.setTimestamp(5, sqlTime);
			pstatement1.executeUpdate();
		
			int imageId;
			
			
			keys = pstatement1.getGeneratedKeys();
			if (keys.next()) {
				imageId = keys.getInt(1);
			}
			else {
				throw new SQLException("Fail creating image");
			}
		
			
			filepath = pathFolder + imageId + fileName;
			
			pstatement2 = con.prepareStatement(query2);
			pstatement2.setString(1, filepath);
			pstatement2.setInt(2, imageId);
			pstatement2.executeUpdate();
			
			for(int i = 0; i<albums.size(); i++) {
				pstatement3[i] = con.prepareStatement(query3[i]);
				pstatement3[i].setInt(1, albums.get(i));
				pstatement3[i].setInt(2, imageId);
				pstatement3[i].executeUpdate();
			}
	
			con.commit();
			return imageId;
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.setAutoCommit(true);
		
			try {
				if (keys != null)
					keys.close();
			} catch (SQLException e1) {
				throw e1;
			}
		
			try {
				if (pstatement1 != null)
					pstatement1.close();
			
				if (pstatement2 != null)
					pstatement2.close();
			
				for(int i = 0; i<albums.size(); i++) {
					if (pstatement3[i] != null)
						pstatement3[i].close();
				}
			} catch (SQLException e2) {
				throw e2;
			}
		}
	}
	
	
	public List<Image> findImagesByAlbum(int id, int numPage) throws SQLException{
		String query = "SELECT * FROM image JOIN catalog ON image.imageId = catalog.imgId WHERE catalog.albId = ? ORDER BY image.creation_date DESC LIMIT 6 OFFSET ?";
		List<Image> images = new ArrayList<>();
		ResultSet result = null;
		PreparedStatement pstatement = null;
		int numIm = numPage*5;
	
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, id);
			pstatement.setInt(2, numIm);
			result = pstatement.executeQuery();
			if(!result.isBeforeFirst())
				return images;
			
			while(result.next()) {
					Image img = new Image();
					img.setId(result.getInt("imageId"));
					img.setTitle(result.getString("title"));
					img.setCreationDate(result.getTimestamp("creation_date"));
					img.setOwner(result.getString("owner"));
					img.setFilePath(result.getString("filepath"));
					img.setDescription(result.getString("description"));
					images.add(img);
			}	
		}catch(SQLException e) {
			throw e;
		}finally {
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e2) {
				throw e2;
			}
		}
		return images;
	}

	
	public Image extractImageById(int id) throws SQLException{
		String query = "SELECT * FROM image WHERE imageId = ?";
		List<Image> images = new ArrayList<>();
		ResultSet result = null;
		PreparedStatement pstatement = null;
	
		Image img = new Image();
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();
			if(!result.isBeforeFirst())
				return null;
			
			result.next();
			img.setId(result.getInt("imageId"));
			img.setTitle(result.getString("title"));
			img.setCreationDate(result.getTimestamp("creation_date"));
			img.setOwner(result.getString("owner"));
			img.setFilePath(result.getString("filepath"));
			img.setDescription(result.getString("description"));
			images.add(img);
				
		}catch(SQLException e) {
			throw e;
		}finally {
			try {
				if (result != null)
					result.close();
			} catch (SQLException e1) {
				throw e1;
			}
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e2) {
				throw e2;
			}
		}
		return img;
	}

	public void deleteImage(int id) throws SQLException {
		con.setAutoCommit(false);
		String query= "DELETE FROM image WHERE imageId= ?";
		PreparedStatement pstatement = null;
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, id);
			pstatement.executeUpdate();
			
			
			con.commit();
				
		}catch(SQLException e) {
			con.rollback();
			throw e;
		}finally {
			con.setAutoCommit(true);
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e2) {
				throw e2;
			}
			
		}
	}
	
}
