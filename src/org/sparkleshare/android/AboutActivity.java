package org.sparkleshare.android;

import org.sparkleshare.android.ui.BaseActivity;

import android.graphics.Color;
import android.os.Bundle;

/**
 * Provides basic information on how to use SparkleShare and this android app
 * @author kai
 *
 */
public class AboutActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		setupActionBar(getString(R.string.info), Color.WHITE);
	}
}
