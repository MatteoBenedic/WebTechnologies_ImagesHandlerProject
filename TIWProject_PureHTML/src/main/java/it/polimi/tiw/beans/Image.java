package it.polimi.tiw.beans;

import java.util.Date;

public class Image {
	private int id;
	private String owner;
	private String filepath;
	private String description;
	private String title;
	private Date creationDate;
	
	public int getId() {
		return id;
	}

	public String getFilePath() {
		return filepath;
	}

	public Date getCreationDate() {
		return creationDate;
	}
	
	public String getOwner() {
		return owner;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setId(int i) {
		id = i;
	}

	public void setTitle(String t) {
		title = t;
	}

	public void setCreationDate(Date d) {
		creationDate = d;
	}
	
	public void setOwner(String o) {
		owner = o;
	}
	
	public void setFilePath(String fp) {
		filepath = fp;
	}
	
	public void setDescription(String dsc) {
		description = dsc;
	}
}
