package org.sparkleshare.android.utils;

public class FormatHelper {

	public static String formatFilesize(String filesize) {
		float Giga = 1024*1024*1024;
		float Mega = 1024*1024;
		float Kilo = 1024;		
		float size = Float.valueOf(filesize);
		if (size > Giga) {
			size /= Giga;
			return String.valueOf(Math.round(size*100.0f)/100.0f) + " GB";
		} else if (size > Mega) {
			size /= Mega;
			return String.valueOf(Math.round(size*10.0f)/10.0f) + " MB";
		} else if (size > Kilo) {
			size /= Kilo;
			return String.valueOf(Math.round(size*10.0f)/10.0f) + " KB";
		} else {
			return String.valueOf(Math.round(size)) + " B";
		}
	}

}
