package org.sparkleshare.android;

import org.sparkleshare.android.actionbarcompat.ActionBarActivity;
import org.sparkleshare.android.actionbarcompat.ActionBarHelper;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
/**
 * Activity for browsing content of a SparkleShare-Dashboard instance.
 * @author kai
 *
 */
public class BrowsingActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.browsing_fragment);
	}
	
	public void setRefreshingState(boolean refreshing) {
		getActionBarHelper().setRefreshActionItemState(refreshing);
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.option, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			SharedPreferences prefs = SettingsActivity.getSettings((ContextWrapper) this);
			String serverUrl = prefs.getString("serverUrl", "");
			Intent overview = new Intent(this, BrowsingActivity.class);
			overview.putExtra("url", serverUrl + "/api/getFolderList");
			startActivity(overview);
			break;
		case R.id.opt_settings:
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			break;
		case R.id.opt_about:
			Intent about = new Intent(this, AboutActivity.class);
			startActivity(about);
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
}
