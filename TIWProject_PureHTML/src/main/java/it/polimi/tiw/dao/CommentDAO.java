package it.polimi.tiw.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.beans.Comment;
import it.polimi.tiw.beans.Image;

public class CommentDAO {
	private Connection con = null;

	public CommentDAO(Connection connection) {
		con = connection;
	}

	public List<Comment> findCommentsByImage(int id) throws SQLException{
		String query = "SELECT * FROM comment WHERE imgId = ?";
		List<Comment> comments = new ArrayList<>();
		ResultSet result = null;
		PreparedStatement pstatement = null;
	
	
		try {
			pstatement = con.prepareStatement(query);
			pstatement.setInt(1, id);
			result = pstatement.executeQuery();
			if(!result.isBeforeFirst())
				return comments;
			
			while(result.next()) {
				Comment cmt = new Comment();
				cmt.setId(result.getInt("id"));
				cmt.setCreator(result.getString("creator"));
				cmt.setImageID(result.getInt("imgId"));
				cmt.setText(result.getString("text"));
				comments.add(cmt);
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
				
		return comments;
	}

	public void createComment(String commt, String user, int id) throws SQLException{
		String query = "INSERT into comment (text, creator, imgId) VALUES(?, ?, ?)";
		PreparedStatement pstatement = null;
		
		try{
			pstatement = con.prepareStatement(query);
			pstatement.setString(1, commt);
			pstatement.setString(2, user);
			pstatement.setInt(3, id);
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
