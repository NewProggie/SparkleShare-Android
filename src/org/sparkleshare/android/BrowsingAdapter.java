package org.sparkleshare.android;

import java.util.ArrayList;

import org.sparkleshare.android.ui.ListEntryItem;
import org.sparkleshare.android.utils.BitmapManager;

import android.content.Context;
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
		viewHolder.icon.setTag(item.getIconUrl());
		BitmapManager.INSTANCE.loadBitmap(item.getIconUrl(), viewHolder.icon, pixelToDp(context, 80), pixelToDp(context, 54));
		return view;
	}

	private int pixelToDp(Context context, int value) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (value * scale + 0.5f);
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
