package org.sparkleshare.android.utils;

public class FormatHelper {

	public static String formatFilesize(String filesize) {
		float size = Float.valueOf(filesize)/1024;
		if (size > (1024*1024)) {
			size /= (1024*1024);
			return String.valueOf(Math.round(size*100.0f)/100.0f) + " GB";
		} else if (size > 1024) {
			size /= 1024;
			return String.valueOf(Math.round(size*10.0f)/10.0f) + " MB";
		} else {
			size = Float.valueOf(filesize)/1024;
			return String.valueOf(Math.round(size)) + " KB";
		}
	}

}
