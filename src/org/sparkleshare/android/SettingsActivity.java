package org.sparkleshare.android;

import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	private Preference releaseAccount;
	private CheckBoxPreference hideFilesFolders;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		addPreferencesFromResource(R.xml.settings);
		
		hideFilesFolders = (CheckBoxPreference) findPreference(getString(R.string.settings_hide_files_folders));
		hideFilesFolders.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				SharedPreferences prefs = getSettings((ContextWrapper) context);
				Editor editor = prefs.edit();
				if (hideFilesFolders.isChecked()) {
					editor.putBoolean("hideFilesFolders", false);
				} else {
					editor.putBoolean("hideFilesFolders", true);
				}
				return editor.commit();
			}
		});
		releaseAccount = (Preference) findPreference(getString(R.string.settings_release_account));
		releaseAccount.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			@Override
			public boolean onPreferenceClick(Preference preference) {
				AlertDialog.Builder builder = new AlertDialog.Builder(context);
				builder.setMessage(getString(R.string.release_account_hint))
					.setTitle(getString(R.string.are_you_sure))
					.setCancelable(false)
					.setPositiveButton(getString(R.string.release), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences prefs = SettingsActivity.getSettings((ContextWrapper) context);
							Editor editor = prefs.edit();
							editor.remove("ident");
							editor.remove("authCode");
							editor.commit();
						}
					}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
		});
		
	}
	
	public static final SharedPreferences getSettings(final ContextWrapper context) {
		String name = context.getPackageName() + "_preferences";
		return context.getSharedPreferences(name, MODE_PRIVATE);
	}

}
