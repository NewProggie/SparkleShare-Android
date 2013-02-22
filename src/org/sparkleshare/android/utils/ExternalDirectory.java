package org.sparkleshare.android.utils;

import java.io.File;

import android.os.Environment;
import org.sparkleshare.android.ui.ListEntryItem;

/**
 * Takes care of state of external storage and creates folder structure for downloaded files
 * 
 * @author kai
 * 
 */
public class ExternalDirectory {

	/**
	 * Takes care of state of external and throws a {@link RuntimeException} if sdcard is unmounted
	 * 
	 * @return path to external storage
	 */
	private static String getExternalRootDirectory() {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File extDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/.sparkleshare/");
			if (extDir.mkdirs() || extDir.exists()) {
				return extDir.getAbsolutePath();
			} else {
				throw new RuntimeException("Couldn't create external directory");
			}
		} else {
			throw new RuntimeException("External Storage is currently not available");
		}
	}
	
	public static void createDirectory(String path){
		File extDir = new File(path);
		
		if(!extDir.exists()){
			if(!extDir.mkdirs()){
				throw new RuntimeException("Couldn't create external directory");
			}
		}
	}
	
	public static String getDownloadTargetPath(ListEntryItem item){
		if(item.getUrl() == null){
			return ExternalDirectory.getExternalRootDirectory() + "/" + item.getTitle();
		}
		
		return ExternalDirectory.getExternalRootDirectory() + "/" + URLPathDecoder.decode(item.getUrl());
	}
	
	public static boolean isMounted() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		} else {
			return false;
		}
	}
}
