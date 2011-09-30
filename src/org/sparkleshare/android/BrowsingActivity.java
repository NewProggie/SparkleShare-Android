package org.sparkleshare.android;

import org.sparkleshare.android.ui.BaseActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ListView;

public class BrowsingActivity extends BaseActivity {
	
	private ListView lv_browsing;
	private BrowsingAdapter adapter;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar("SparkleShare", Color.WHITE);
		context = this;
		
		lv_browsing = new ListView(context);
		adapter = new BrowsingAdapter(context);
		lv_browsing.setAdapter(adapter);
		
		setContentView(lv_browsing);
	}
	
	
}
