package org.sparkleshare.android.ui;

import org.sparkleshare.android.utils.FormatHelper;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

public class ListEntryItem implements Comparable<ListEntryItem>, Parcelable {

	private String title, subtitle;
	private String id;
	private String type;
	private String url;
	private String mime;
	private String mimeBase;
	private String filesize;
        private String filePath;
	private int listviewPosition;

	public ListEntryItem() {
	}

	public ListEntryItem(Parcel source) {
		Bundle b = source.readBundle();
		title = b.getString("title");
		subtitle = b.getString("subtitle");
		id = b.getString("id");
		url = b.getString("url");
		mime = b.getString("mime");
		mimeBase = b.getString("mimeBase");
		filesize = b.getString("filesize");
		listviewPosition = b.getInt("listviewPosition");
                filePath = b.getString("filePath");
	}

	public int getListviewPosition() {
		return listviewPosition;
	}

	public void setListviewPosition(int listviewPosition) {
		this.listviewPosition = listviewPosition;
	}
	
	public String getMimeBase() {
		return mimeBase;
	}

	public void setMimeBase(String mimeBase) {
		this.mimeBase = mimeBase;
	}

	public String getFilesize() {
		return filesize;
	}

	public void setFilesize(String filesize) {
		this.filesize = filesize;
	}

        public void setFilePath(String path) {
            this.filePath = path;
        }
        
        public String getFilePath() {
            return this.filePath;
        }
        
	public String getMimetype() {
		return mime;
	}
        
        public boolean isDirectory() {

            if(type.equals("dir")){
                return true;
            }
            
            return false;
        }

	public void setMimetype(String mimetype) {
		this.mime = mimetype;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubtitle() {
		if (filesize != null) {
			return FormatHelper.formatFilesize(filesize);
		} else {
			return subtitle;
		}
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public int compareTo(ListEntryItem another) {
		if (type.equals("file")) {
			if (another.type.equals("dir")) {
				return 1;
			} else {
				return title.compareToIgnoreCase(another.title);
			}
		} else {
			/* type.equals("dir) */
			if (another.type.equals("file")) {
				return -1;
			} else {
				return title.compareToIgnoreCase(another.title);
			}
		}
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		Bundle b = new Bundle();
		b.putString("title", title);
		b.putString("subtitle", subtitle);
		b.putString("id", id);
		b.putString("url", url);
		b.putString("mime", mime);
		b.putString("mimeBase", mimeBase);
		b.putString("filesize", filesize);
		b.putInt("listviewPosition", listviewPosition);
                b.putString("filePath", filePath);
		dest.writeBundle(b);
	}

	public static final Parcelable.Creator<ListEntryItem> CREATOR = new Creator<ListEntryItem>() {

		@Override
		public ListEntryItem createFromParcel(Parcel source) {
			return new ListEntryItem(source);
		}

		@Override
		public ListEntryItem[] newArray(int size) {
			return new ListEntryItem[size];
		}
	};

}
