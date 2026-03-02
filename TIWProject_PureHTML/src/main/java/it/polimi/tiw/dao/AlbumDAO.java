package it.polimi.tiw.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Album;

public class AlbumDAO {
	private Connection con;
	
	public AlbumDAO(Connection connection) {
		this.con = connection;
	}
	
	public List<Album> findAlbumByUser(String usrn) throws SQLException{
		String query = "SELECT * FROM album WHERE creator = ? ORDER BY creation_date DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		List<Album> usrAlbs = new ArrayList<>();
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, usrn);
			result = pstatement.executeQuery();
			if(!result.isBeforeFirst())
				return null;
			else 
				while(result.next()) {
					Album a = new Album();
					a.setId(result.getInt("albumid"));
					a.setTitle(result.getString("title"));
					a.setCreationDate(result.getTimestamp("creation_date"));
					a.setCreator(result.getString("creator"));
					usrAlbs.add(a);
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
		return usrAlbs;
	}
	
	
	public List<Album> findAlbumByOthers(String usrn) throws SQLException{
		String query = "SELECT * FROM album WHERE creator != ? ORDER BY creation_date DESC";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		List<Album> usrAlbs = new ArrayList<>();
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, usrn);
			result = pstatement.executeQuery();
			if(!result.isBeforeFirst())
				return null;
			else 
				while(result.next()) {
					Album a = new Album();
					a.setId(result.getInt("albumid"));
					a.setTitle(result.getString("title"));
					a.setCreationDate(result.getTimestamp("creation_date"));
					a.setCreator(result.getString("creator"));
					usrAlbs.add(a);
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
		return usrAlbs;
	}
	
	
	public void createAlbum(String name, String owner) throws SQLException{
		String query = "INSERT into album (title, creator, creation_date) VALUES(?, ?, ?)";
		PreparedStatement pstatement = null;
		java.util.Date utilTime = new java.util.Date();
		java.sql.Timestamp sqlTime = new java.sql.Timestamp(utilTime.getTime());
		try{
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, name);
			pstatement.setString(2, owner);
			pstatement.setTimestamp(3, sqlTime);
			pstatement.executeUpdate();  
		}catch(SQLException e){
			throw e;
		}finally {
			try {
				if (pstatement != null)
					pstatement.close();
			} catch (SQLException e1) {
				throw e1;
			}
		}
	}
	
	
	
}
