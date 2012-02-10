package org.sparkleshare.android;

import org.sparkleshare.android.actionbarcompat.ActionBarActivity;

import android.os.Bundle;

/**
 * Provides basic information on how to use SparkleShare and this android app
 * @author kai
 *
 */
public class AboutActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_fragment);
		setTitle(R.string.info);
	}
}
