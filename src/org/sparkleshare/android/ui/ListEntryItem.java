package org.sparkleshare.android.ui;

import android.graphics.Bitmap;

public class ListEntryItem {

	private String title, subtitle;
	private Bitmap icon;
	private String id;
	private String type;
	private String url;
	private String mimetype;
	private String filesize;
	
	public String getFilesize() {
		return filesize;
	}
	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		if (filesize != null) {
			float size = Float.valueOf(filesize)/1024;
			return String.valueOf(Math.round(size)) + " KB";
		} else {
			return subtitle;
		}	
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Bitmap getIcon() {
		return icon;
	}
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
	
}
