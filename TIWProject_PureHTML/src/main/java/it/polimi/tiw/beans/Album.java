package it.polimi.tiw.beans;

import java.util.Date;

public class Album {
	private int id;
	private String title;
	private Date creationdate;
	private String creator;
	
	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Date getCreationDate() {
		return creationdate;
	}
	
	public String getCreator() {
		return creator;
	}
	
	public void setId(int i) {
		id = i;
	}

	public void setTitle(String t) {
		title = t;
	}

	public void setCreationDate(Date d) {
		creationdate = d;
	}
	
	public void setCreator(String c) {
		creator = c;
	}
}
