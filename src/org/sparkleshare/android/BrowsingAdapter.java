package org.sparkleshare.android;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import org.sparkleshare.android.ui.ListEntryItem;
import org.sparkleshare.android.utils.ExternalDirectory;
import org.sparkleshare.android.utils.MimetypeChecker;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BrowsingAdapter extends BaseAdapter {

	private ArrayList<ListEntryItem> items;
	private Context context;
	private Boolean hideFilesFolders;
	
	public BrowsingAdapter(Context context) {
		this.context = context;
		items = new ArrayList<ListEntryItem>();
		SharedPreferences prefs = SettingsActivity.getSettings((ContextWrapper) context);
		hideFilesFolders = prefs.getBoolean("hideFilesFolders", false);
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	public void addEntry(ListEntryItem entry) {
		if (hideFilesFolders && entry.getTitle().startsWith(".")) {
			return;
		} else {
			items.add(entry);
			Collections.sort(items);
			notifyDataSetChanged();
		}
	}
	
	@Override
	public Object getItem(int position) {
		return items.get(position);
	}
	
	public void setItem(ListEntryItem item, int listviewPosition) {
		items.set(listviewPosition, item);
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		Viewholder viewHolder;
		if (convertView == null) {
			view = LayoutInflater.from(context).inflate(R.layout.list_item_entry, null);
			TextView title = (TextView) view.findViewById(R.id.list_item_title);
			TextView subtitle = (TextView) view.findViewById(R.id.list_item_subtitle);
			ImageView icon = (ImageView) view.findViewById(R.id.list_item_icon);
			viewHolder = new Viewholder(title, subtitle, icon);
			view.setTag(viewHolder);
		} else {
			viewHolder = (Viewholder) convertView.getTag();
		}
		ListEntryItem item = items.get(position);
		viewHolder.title.setText(item.getTitle());
		File file = new File(ExternalDirectory.getExternalRootDirectory() + "/" + item.getTitle());
		if (file.exists()) {
			viewHolder.subtitle.setText("✔ " + item.getSubtitle());
		} else {
			viewHolder.subtitle.setText(item.getSubtitle());
		}
		
		if (item.getMimetype() != null) {
			viewHolder.icon.setImageResource(MimetypeChecker.getResIdforMimetype(item.getMimetype()));
		}
		return view;
	}


	private class Viewholder {
		TextView title, subtitle;
		ImageView icon;
		
		public Viewholder(TextView title, TextView subtitle, ImageView icon) {
			super();
			this.icon = icon;
			this.title = title;
			this.subtitle = subtitle;
		}
	}

}
