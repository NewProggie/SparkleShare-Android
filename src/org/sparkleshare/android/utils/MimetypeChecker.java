package org.sparkleshare.android.utils;

import org.sparkleshare.android.R;

public class MimetypeChecker {

	public static int getResIdforMimetype(String mimetype) {
		if (mimetype.contains("application")) {
			return R.drawable.ic_application;
		} else if (mimetype.contains("image")) {
			return R.drawable.ic_image;
		} else if (mimetype.contains("text")) {
			return R.drawable.ic_text;
		} else if (mimetype.contains("dir")) {
			return R.drawable.ic_folder;
		} else if (mimetype.contains("audio")) {
			return R.drawable.ic_audio;
		} else if (mimetype.contains("video")) {
			return R.drawable.ic_video;
		} else {
			return R.drawable.ic_text;
		}
	}

}
