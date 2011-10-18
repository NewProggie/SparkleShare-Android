package org.sparkleshare.android;

import org.sparkleshare.android.ui.BaseActivity;
import org.sparkleshare.android.ui.ListEntryItem;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class FileDetailsActivity extends BaseActivity {

	ImageView fileIcon;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_details);
		
		fileIcon = (ImageView) findViewById(R.id.iv_file_icon);
		ListEntryItem item;;
	}
	
	public void buttonClick(View target) {
		switch (target.getId()) {
		case R.id.btn_download_file:
			break;
		case R.id.btn_open_file:
			break;
		}
	}
}
