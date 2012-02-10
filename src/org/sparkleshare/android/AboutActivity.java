package org.sparkleshare.android;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Provides basic information on how to use SparkleShare and this android app
 * @author kai
 *
 */
public class AboutActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_fragment);
	}
}
