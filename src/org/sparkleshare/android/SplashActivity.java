package org.sparkleshare.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

/**
 * Splash {@link Activity} which will be shown to user when no previously saved credentials could be found.
 * 
 * @author kai
 * 
 */
public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		/* Found credentials, forwarding to BrowsingActivity */
		SharedPreferences prefs = SettingsActivity.getSettings(this);
		if (prefs.contains("ident")) {
			Intent browseData = new Intent(this, BrowsingActivity.class);
			String serverUrl = prefs.getString("serverUrl", "");
			browseData.putExtra("url", serverUrl + "/api/getFolderList");
			startActivity(browseData);
			this.finish();
		}
	}

}
