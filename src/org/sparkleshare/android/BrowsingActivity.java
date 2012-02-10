package org.sparkleshare.android;

import org.sparkleshare.android.actionbarcompat.ActionBarActivity;

import android.content.res.Configuration;
import android.os.Bundle;
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
	

	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}
