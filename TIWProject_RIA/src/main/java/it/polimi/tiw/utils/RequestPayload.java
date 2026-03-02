package it.polimi.tiw.utils;

import java.util.List;

public class RequestPayload {
	 	private int albumId;
	    private List<Integer> ids;

	    // Getters and setters
	    public int getAlbumId() {
	        return albumId;
	    }

	    public void setAlbumId(int i) {
	        this.albumId = i;
	    }

	    public List<Integer> getIds() {
	        return ids;
	    }

	    public void setIds(List<Integer> is) {
	        this.ids = is;
	    }
}
