package it.polimi.tiw.dao;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.beans.User;

public class UserDAO {
	private Connection con;

	public UserDAO(Connection connection) {
		this.con = connection;
	}

	public User checkCredentials(String usrn, String pwd) throws SQLException{
		String query = "SELECT username, email FROM Users WHERE username = ? AND password =?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try {
			pstatement = con.prepareStatement(query); 
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			result = pstatement.executeQuery(); 
			if (!result.isBeforeFirst()) // no results, credential check failed
				return null;
			else {
				result.next();
				User user = new User();
				user.setEmail(result.getString("email"));
				user.setUsername(result.getString("username"));
				return user;
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
		
	}
	
	public boolean checkUniqueUsername(String usrn) throws SQLException{
		String query = "SELECT username, email FROM Users WHERE username = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try{
			pstatement = con.prepareStatement(query);
			pstatement.setString(1,  usrn);
			result= pstatement.executeQuery();
			
			if(!result.isBeforeFirst())
				return true;
			else
				return false;
			
		}catch(SQLException e) {
			throw e;
		}finally{
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
	}
	
	
	public boolean checkUniqueMail(String mail) throws SQLException{
		String query = "SELECT username, email FROM Users WHERE email = ?";
		ResultSet result = null;
		PreparedStatement pstatement = null;
		try{
			pstatement = con.prepareStatement(query);
			pstatement.setString(1,  mail);
			result= pstatement.executeQuery();
			
			if(!result.isBeforeFirst())
				return true;
			else
				return false;
			
		}catch(SQLException e) {
			throw e;
		}finally{
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
	}
	
	
	public void createUser(String usrn, String pwd, String em) throws SQLException{
		String query = "INSERT into Users (username, password, email) VALUES(?, ?, ?)";
		PreparedStatement pstatement = null;
		try{
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, usrn);
			pstatement.setString(2, pwd);
			pstatement.setString(3, em);
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
