package org.sparkleshare.android;

import java.net.URLConnection;
import java.util.ArrayList;

import org.sparkleshare.android.ui.ListEntryItem;

import android.content.Context;
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
	
	public BrowsingAdapter(Context context) {
		this.context = context;
		items = new ArrayList<ListEntryItem>();
	}
	
	@Override
	public int getCount() {
		return items.size();
	}

	public void addEntry(ListEntryItem entry) {
		items.add(entry);
		notifyDataSetChanged();
	}
	
	@Override
	public Object getItem(int position) {
		return items.get(position);
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
		viewHolder.subtitle.setText(item.getSubtitle());
		if (item.getMimetype() != null) {
			String mime = item.getMimetype();
			if (mime.contains("application")) {
				viewHolder.icon.setImageResource(R.drawable.ic_application);
			} else if (mime.contains("image")) {
				viewHolder.icon.setImageResource(R.drawable.ic_image);
			} else if (mime.contains("text")) {
				viewHolder.icon.setImageResource(R.drawable.ic_text);
			} else if (mime.contains("dir")) {
				viewHolder.icon.setImageResource(R.drawable.ic_folder);
			} else if (mime.contains("audio")) {
				viewHolder.icon.setImageResource(R.drawable.ic_audio);
			} else {
				viewHolder.icon.setImageResource(R.drawable.ic_package);
			}
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
