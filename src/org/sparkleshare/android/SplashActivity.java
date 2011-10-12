package org.sparkleshare.android;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Splash {@link Activity} which will be shown to user when no previously saved credentials could be found.
 * 
 * @author kai
 * 
 */
public class SplashActivity extends Activity {

	private Context context;
	private Button btnScanQRCode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.splash);
		btnScanQRCode = (Button) findViewById(R.id.btn_scan_qrcode);
		btnScanQRCode.setEnabled(isQRCodeAvailable(context));

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

	/**
	 * Will be called when user clicks a button inside this {@link Activity}
	 * 
	 * @param target Button which was clicked by user
	 */
	public void buttonClick(View target) {
		switch (target.getId()) {
		case R.id.btn_insert_linkcode:
			Intent setup = new Intent(context, SetupActivity.class);
			startActivity(setup);
			break;
		case R.id.btn_scan_qrcode:
			Intent intent = new Intent("com.google.zxing.client.android.SCAN");
			intent.setPackage("com.google.zxing.client.android");
			intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
			PackageManager packageManager = getPackageManager();
			List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
			if (list.size() > 0) {
				startActivityForResult(intent, 0);	
			} else {
				Toast.makeText(context, getString(R.string.scanner_app_not_found), Toast.LENGTH_SHORT).show();
			}
			
			break;
		}
	}

	/**
	 * Checks for the barcode scanner app
	 * @param context current context
	 * @return true if barcode scanner app is installed on this device, else false
	 */
	private boolean isQRCodeAvailable(Context context) {
		final PackageManager pManager = context.getPackageManager();
		final Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		List<ResolveInfo> list = pManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0) {
			if (resultCode == RESULT_OK) {
				String content = data.getStringExtra("SCAN_RESULT");
				String url = content.split("SSHARE:")[1].split("#")[0];
				String linkcode = content.split("#")[1];
				Intent setup = new Intent(context, SetupActivity.class);
				setup.putExtra("url", url);
				setup.putExtra("linkcode", linkcode);
				startActivity(setup);
			}
		}
	}

}
