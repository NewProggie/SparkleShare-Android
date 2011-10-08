package org.sparkleshare.android.ui;

import org.sparkleshare.android.BrowsingActivity;
import org.sparkleshare.android.R;
import org.sparkleshare.android.SettingsActivity;
import org.sparkleshare.android.SetupActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Implements common functionality across the different app activities such as the actionbar. Ths class
 * mustn't be used directly, instead activities should inherit from
 * 
 * @author kai
 * 
 */
public class BaseActivity extends Activity {

	private ViewGroup content;
	private boolean optionsMenuEnabled;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.base_activity);
		content = (ViewGroup) super.findViewById(R.id.layout_container);
		optionsMenuEnabled = true;
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		content.removeAllViews();
		getLayoutInflater().inflate(layoutResID, content);
	}
	
	@Override
	public void setContentView(View view) {
		content.removeAllViews();
		content.addView(view);
	}
	
	@Override
	public View findViewById(int id) {
		return content.findViewById(id);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * Sets up the actionbar with the given title and the color. If the title is
	 * null, then the app name will be shown instead of the title. Otherwise, a
	 * home button and the given title are visible. If the color is null, then
	 * the default color is visible.
	 * 
	 * @param title title of actionbar
	 * @param color color of title
	 */
	protected void setupActionBar(CharSequence title, int color) {
		final ViewGroup actionBar = getActionBar();
		if (actionBar == null) {
			return;
		}
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT);
		layoutParams.weight = 1;
		
		View.OnClickListener homeClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				backToMain();
			}
		};
		
		if (title != null) {
			// adding home button
			addActionButton(R.drawable.ic_action_overview, R.string.home, homeClickListener, true);
			
			// adding title text
			TextView titleText = new TextView(this, null, R.attr.actionbarTextStyle);
			titleText.setLayoutParams(layoutParams);
			titleText.setText(title);
			actionBar.addView(titleText);
		} else {
			// adding app name
			TextView appName = new TextView(this, null, R.attr.actionbarTextStyle);
			appName.setText(getString(R.string.app_name));
			actionBar.addView(appName);
			
			// adding layout for aligning items to the right
			View dummy = new View(this);
			dummy.setLayoutParams(layoutParams);
			actionBar.addView(dummy);
		}
		
	}
	
	protected void setupActionBarWithoutHomeButton(CharSequence title, int color) {
		final ViewGroup actionBar = getActionBar();
		if (actionBar == null) {
			return;
		}
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT);
		layoutParams.weight = 1;
		
		if (title != null) {
			// adding title text
			TextView titleText = new TextView(this, null, R.attr.actionbarTextStyle);
			titleText.setLayoutParams(layoutParams);
			titleText.setText(title);
			actionBar.addView(titleText);
		} else {
			// adding app name
			TextView appName = new TextView(this, null, R.attr.actionbarTextStyle);
			appName.setText(getString(R.string.app_name));
			actionBar.addView(appName);
			
			// adding layout for aligning items to the right
			View dummy = new View(this);
			dummy.setLayoutParams(layoutParams);
			actionBar.addView(dummy);
		}
	}
	
	/**
	 * Returns the {@link ViewGroup} for the actionbar. May return null.
	 */
	protected ViewGroup getActionBar() {
		return (ViewGroup) super.findViewById(R.id.actionbar);
	}
	
	/**
	 * Disables the actionbar.
	 */
	protected void disableActionBar() {
		ViewGroup actionBar = (ViewGroup) super.findViewById(R.id.actionbar);
		actionBar.setVisibility(View.GONE);
	}
	
	/**
	 * Interface for activities who need to draw action buttons on the action bar
	 * @param iconResId ressource identifier for icon
	 * @param textResId ressource identifier for text
	 * @param clickListener triggered listener when pressing the button
	 */
	protected void addNewActionButton(int iconResId, int textResId, View.OnClickListener clickListener) {
		addActionButton(iconResId, textResId, clickListener, false);
	}
	
	/**
	 * Adds new action button onto the action bar and takes car of drawing the separator between each buttons on the right place
	 * @param iconResId resource identifier for icon
	 * @param textResId resource identifier for text
	 * @param clickListener triggered listener when pressing the button
	 * @param separatorAfter true if there is another button following, else false
	 * @return new action button view
	 */
	private View addActionButton(int iconResId, int textResId, View.OnClickListener clickListener, boolean separatorAfter) {
		final ViewGroup actionBar = getActionBar();
		
		// button separator
		ImageView separator = new ImageView(getApplicationContext(), null, R.attr.actionbarSeparatorStyle);
		separator.setLayoutParams(new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));
		separator.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_separator));
		
		// new action button
		ImageButton actionButton = new ImageButton(this, null, R.attr.actionbarButtonStyle);
		actionButton.setImageResource(iconResId);
		actionButton.setContentDescription(this.getResources().getString(textResId));
		actionButton.setOnClickListener(clickListener);
		
		// adding separator properly
		if (!separatorAfter)
			actionBar.addView(separator);
		
		actionBar.addView(actionButton);
		
		if (separatorAfter)
			actionBar.addView(separator);
		
		return actionButton;
	}
	
	/**
	 * Starting home activity, returning to
	 * {@link MainActivity }.
	 */
	private void backToMain() {
		SharedPreferences prefs = SettingsActivity.getSettings(this);
		String serverUrl = prefs.getString("serverUrl", "") + "/api/getFolderList";
		
		Intent intent = new Intent(this, BrowsingActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("url", serverUrl);
		startActivity(intent);
	}
	
	protected void disableOptionsMenu() {
		optionsMenuEnabled = false;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (optionsMenuEnabled) {
			getMenuInflater().inflate(R.menu.option, menu);
			return super.onCreateOptionsMenu(menu);
		} else {
			return false;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.opt_settings:
			Intent settings = new Intent(this, SettingsActivity.class);
			startActivity(settings);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
