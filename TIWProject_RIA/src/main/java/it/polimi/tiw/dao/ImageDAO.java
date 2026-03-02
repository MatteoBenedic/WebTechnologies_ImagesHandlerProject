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
		
		String query1 = "INSERT INTO images(title, description, filepath, owner, creation_date) VALUES(?, ?, ?, ?, ?)";
		PreparedStatement pstatement1 = null;
		ResultSet keys = null;
		
		String query2 = "UPDATE images SET filepath = ? WHERE imageId = ?";
		PreparedStatement pstatement2 = null;
		
		String[] query3 = new String[albums.size()];
		PreparedStatement[] pstatement3 = new PreparedStatement[albums.size()];
		for(int i = 0; i<albums.size(); i++) {
			query3[i]= "SELECT max(ord) AS ordvalue FROM collection WHERE albId = ?";
			pstatement3[i] = null;
		}
		ResultSet result = null;
				
		String[] query4 = new String[albums.size()];
		PreparedStatement[] pstatement4 = new PreparedStatement[albums.size()];
		for(int i = 0; i<albums.size(); i++) {
			query4[i] = "INSERT INTO collection(albId, imgId, ord) VALUES(?, ?, ?)";
			pstatement4[i] = null;
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
			
			int a;
			List<Integer> order = new ArrayList<>();
			for(int i = 0; i<albums.size(); i++) {
				pstatement3[i] = con.prepareStatement(query3[i]);
				pstatement3[i].setInt(1, albums.get(i));
			
				
				result = pstatement3[i].executeQuery();
				if(!result.isBeforeFirst())
					order.add(0);
				else {
					while(result.next()) {
						a = result.getInt("ordvalue");
						if(a == 0) {
							order.add(a);
						}else {
							order.add(a+1);
						}
					}

				}
			}
		
			
			for(int i = 0; i<albums.size(); i++) {
				pstatement4[i] = con.prepareStatement(query4[i]);
				pstatement4[i].setInt(1, albums.get(i));
				pstatement4[i].setInt(2, imageId);
				pstatement4[i].setInt(3, order.get(i));
				pstatement4[i].executeUpdate();
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
	
	
	public List<Image> findImagesByAlbum(int id) throws SQLException{
		String query1 = "SELECT * FROM images JOIN collection ON images.imageId = collection.imgId WHERE collection.albId = ? ORDER BY images.creation_date DESC";
		String query2 = "SELECT * FROM images JOIN collection ON images.imageId = collection.imgId WHERE collection.albId = ? ORDER BY collection.ord DESC";
		String query = "SELECT max(ord) AS ordvalue from collection WHERE albId = ?";
		List<Image> images = new ArrayList<>();
		ResultSet result = null;
		ResultSet result1 = null;
		ResultSet result2 = null;
		PreparedStatement pstatement = null;
		PreparedStatement pstatement1 = null;
		PreparedStatement pstatement2 = null;
		
		
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();
			if(!result.isBeforeFirst())
				return images;
	
			
			while(result.next()) {
				int a = result.getInt("ordvalue");
				
				if(a != 0) {
					pstatement2 = con.prepareStatement(query2);
					pstatement2.setInt(1, id);
					result2 = pstatement2.executeQuery();
					if(!result2.isBeforeFirst())
						return images;
					
					while(result2.next()) {
						Image img = new Image();
						img.setId(result2.getInt("imageId"));
						img.setTitle(result2.getString("title"));
						img.setCreationDate(result2.getTimestamp("creation_date"));
						img.setOwner(result2.getString("owner"));
						img.setFilePath(result2.getString("filepath"));
						img.setDescription(result2.getString("description"));
						images.add(img);
					}
					
				}else {
					pstatement1 = con.prepareStatement(query1);
					pstatement1.setInt(1, id);
					result1 = pstatement1.executeQuery();
					if(!result1.isBeforeFirst())
						return images;
					
					while(result1.next()) {
						Image img = new Image();
						img.setId(result1.getInt("imageId"));
						img.setTitle(result1.getString("title"));
						img.setCreationDate(result1.getTimestamp("creation_date"));
						img.setOwner(result1.getString("owner"));
						img.setFilePath(result1.getString("filepath"));
						img.setDescription(result1.getString("description"));
						images.add(img);
					}	
				}
			}
			
			
		}catch(SQLException e) {
			throw e;
		}finally {
			try {
				if (result != null)
					result.close();
				if (result1 != null)
					result1.close();
				if (result2 != null)
					result2.close();
				
			} catch (SQLException e1) {
				throw e1;
			}
			try {
				if (pstatement != null)
					pstatement.close();
				if (pstatement1 != null)
					pstatement1.close();
				if (pstatement2 != null)
					pstatement2.close();
			} catch (SQLException e2) {
				throw e2;
			}
		}
		return images;
	}

	public Image extractImageById(int id) throws SQLException{
		String query = "SELECT * FROM images WHERE imageId = ?";
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
		String query= "DELETE FROM images WHERE imageId= ?";
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

	public void setOrder(int albumId, List<Integer> ids) throws SQLException{
		System.out.println("4");
		con.setAutoCommit(false);
		String[] query = new String[ids.size()];
				
		PreparedStatement[] pstatement = new PreparedStatement[ids.size()];
		for(int i = 0; i< ids.size(); i++) {
			query[i]= "UPDATE collection SET ord = ? WHERE  albId= ? AND imgId = ?";
			pstatement[i] = null;
		}
		System.out.println("5");
		try {
			for(int i = 0; i<ids.size(); i++) {
				pstatement[i] = con.prepareStatement(query[i]);
				pstatement[i].setInt(1, ids.size()- i);
				pstatement[i].setInt(2, albumId);
				pstatement[i].setInt(3, ids.get(i));
				pstatement[i].executeUpdate();
			}

			System.out.println("6");
			con.commit();
			
		} catch (SQLException e) {
			con.rollback();
			throw e;
		} finally {
			con.setAutoCommit(true);
		
			try {
				for(int i = 0; i<ids.size(); i++) {
					if (pstatement[i] != null)
						pstatement[i].close();
				}
			} catch (SQLException e2) {
				throw e2;
			}
		}
		
		
	}
	
}
