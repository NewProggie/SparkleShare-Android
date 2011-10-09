package org.sparkleshare.android.ui;

import android.graphics.Bitmap;

public class ListEntryItem implements Comparable<ListEntryItem> {

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
			return FormatHelper.formatFilesize(filesize);
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
	@Override
	public int compareTo(ListEntryItem another) {
		if (type.equals("file")) {
			if (another.type.equals("dir")) {
				return 1;
			} else {
				return title.compareToIgnoreCase(another.title);
			}
		} else {
			/* type.equals("dir) */
			if (another.type.equals("file")) {
				return -1;
			} else {
				return title.compareToIgnoreCase(another.title);
			}
		}
	}
	
}
