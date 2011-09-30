package org.sparkleshare.android.ui;

import android.graphics.Bitmap;

public class ListEntryItem {

	private String title, subtitle;
	private Bitmap icon;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSubtitle() {
		return subtitle;
	}
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	public Bitmap getIcon() {
		return icon;
	}
	public void setIcon(Bitmap icon) {
		this.icon = icon;
	}
	public String getIconUrl() {
		// TODO Auto-generated method stub
		return "";
	}
	
}
