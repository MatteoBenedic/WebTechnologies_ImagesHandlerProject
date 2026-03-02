package it.polimi.tiw.beans;

public class Comment {
	private int id;
	private String creator;
	private String text;
	private int imgId;
	
	public String getCreator() {
		return creator;
	}
	
	public String getText() {
		return text;
	}
	
	public int getImageID(){
		return imgId;
	}
	
	public int getId() {
		return id;
	}
	
	public void setCreator(String usr){
		creator = usr;
	}
	
	public void setText(String t) {
		text = t;
	}
	
	public void setImageID(int i) {
		imgId = i;
	}
	
	public void setId(int i) {
		id = i;
	}
}
