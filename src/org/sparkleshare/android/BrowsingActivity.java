package org.sparkleshare.android;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sparkleshare.android.ui.BaseActivity;
import org.sparkleshare.android.ui.ListEntryItem;
import org.sparkleshare.android.utils.ExternalDirectory;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BrowsingActivity extends BaseActivity {
	
	private ListView lv_browsing;
	private BrowsingAdapter adapter;
	private Context context;
	private String ident, authCode, serverUrl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupActionBar("SparkleShare", Color.WHITE);
		context = this;
		
		lv_browsing = new ListView(context);
		adapter = new BrowsingAdapter(context);
		lv_browsing.setAdapter(adapter);
		lv_browsing.setOnItemClickListener(onListItemClick());
		setContentView(lv_browsing);
		SharedPreferences prefs = SettingsActivity.getSettings((ContextWrapper) context);
		ident = prefs.getString("ident", "");
		authCode = prefs.getString("authCode", "");
		serverUrl = prefs.getString("serverUrl", "");
		
		String url = getIntent().getStringExtra("url");
		new DownloadFileList().execute(url);
	}
	
	private OnItemClickListener onListItemClick() {
		OnItemClickListener listener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListEntryItem current = (ListEntryItem) adapter.getItem(position);
				if (current.getType().equals("dir")) {
					Intent browseFolder = new Intent(context, BrowsingActivity.class);
					browseFolder.putExtra("url", serverUrl + "/api/getFolderContent/" + current.getId() + "?" + current.getUrl());
					startActivity(browseFolder);
				} else if (current.getType().equals("git")) {
					Intent browseFolder = new Intent(context, BrowsingActivity.class);
					browseFolder.putExtra("url", serverUrl + "/api/getFolderContent/" + current.getId());
					startActivity(browseFolder);
				} else if (current.getType().equals("file")) {
					File file = new File(ExternalDirectory.getExternalRootDirectory() + "/" + current.getTitle());
					if (file.exists()) {
						Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getAbsolutePath()));
						String mime = URLConnection.guessContentTypeFromName(file.getAbsolutePath());
						open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						open.setAction(android.content.Intent.ACTION_VIEW);
						open.setDataAndType((Uri.fromFile(file)), mime);
						startActivity(open);
					} else {
						StringBuilder sb = new StringBuilder();
						sb.append(serverUrl);
						sb.append("/api/getFile/");
						sb.append(current.getId() + "?");
						sb.append(current.getUrl());
						current.setUrl(sb.toString());
						new DownloadFile().execute(current);
					}
				}
				
			}
		};
		return listener;
	}
	
	private class DownloadFile extends AsyncTask<ListEntryItem, Integer, Boolean> {
		
		@Override
		protected void onPreExecute() {
			// TODO: Progressdialog in action bar
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(ListEntryItem... params) {
			// TODO: Check for connectivity
			ListEntryItem current = params[0];
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(current.getUrl());
				Log.d("url", current.getUrl());
				get.setHeader("X-SPARKLE-IDENT", ident);
				get.setHeader("X-SPARKLE-AUTH", authCode);
				HttpEntity entity = client.execute(get).getEntity();
				if (entity != null) {
					File file = new File(ExternalDirectory.getExternalRootDirectory() + "/" + current.getTitle());
					OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
					entity.writeTo(out);
					entity.consumeContent();
					out.close();
				}
			} catch (ClientProtocolException e) {
				Log.e("DownloadFile", e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				Log.e("DownloadFile", e.getLocalizedMessage());
				return false;
			}
			return true;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) {
			// TODO: Implement Notification showing download progress
			super.onProgressUpdate(values);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
	
	
	private class DownloadFileList extends AsyncTask<String, ListEntryItem, Boolean> {
		
		@Override
		protected void onPreExecute() {
			// TODO: Progressdialog in action bar
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			String server = params[0];
			try {
				// TODO: Refactor I/O here and in SetupActivity to central place
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet(server);
				get.setHeader("X-SPARKLE-IDENT", ident);
				get.setHeader("X-SPARKLE-AUTH", authCode);
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					StringBuffer sb = new StringBuffer();
					String line = "";
					String NL = System.getProperty("line.separator");
					while ((line = in.readLine()) != null) {
						sb.append(line + NL);
					}
					in.close();
					JSONArray folderList = new JSONArray(sb.toString());
					for (int i=0; i<folderList.length(); i++) {
						JSONObject json = folderList.getJSONObject(i);
						ListEntryItem item = new ListEntryItem();
						item.setTitle(json.getString("name"));
						item.setId(json.getString("id"));
						item.setType(json.getString("type"));
						if (json.has("url")) {
							item.setUrl(json.getString("url"));
						}
						publishProgress(item);
					}
				}
			} catch (ClientProtocolException e) {
				Log.e("Browsing failed", e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				Log.e("Browsing failed", e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				Log.e("Browsing failed", e.getLocalizedMessage());
				return false;
			}
			return true;
		}
		
		@Override
		protected void onProgressUpdate(ListEntryItem... values) {
			adapter.addEntry(values[0]);
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}
	}
	
}
