package org.sparkleshare.android.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.sparkleshare.android.BrowsingActivity;
import org.sparkleshare.android.BrowsingAdapter;
import org.sparkleshare.android.FileDetailsActivity;
import org.sparkleshare.android.R;
import org.sparkleshare.android.SettingsActivity;
import org.sparkleshare.android.utils.FakeSocketFactory;

import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class BrowsingFragment extends Fragment {

	private static final String TAG = "BrowsingFragment";
	private ListView lvBrowsing;
	private BrowsingAdapter adapter;
	private String ident, authCode, serverUrl, folderId, currentUrl;
	private String foldername = "SparkleShare";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		lvBrowsing = new ListView(getActivity());
		adapter = new BrowsingAdapter(getActivity());
		lvBrowsing.setAdapter(adapter);
		lvBrowsing.setOnItemClickListener(onListItemClick());
		
		
		SharedPreferences prefs = SettingsActivity.getSettings((ContextWrapper) getActivity());
		ident = prefs.getString("ident", "");
		authCode = prefs.getString("authCode", "");
		serverUrl = prefs.getString("serverUrl", "");
		folderId = prefs.getString("folderId", "");
		
		if (getActivity().getIntent().hasExtra("foldername")) {
			foldername = getActivity().getIntent().getStringExtra("foldername");
		}
		currentUrl = getActivity().getIntent().getStringExtra("url");
		new DownloadFileList().execute(currentUrl);
		return lvBrowsing;
	}
	
	/**
	 * Will be called everytime an item on this activities' listview was clicked.
	 * @return newly created {@link OnItemClickListener}
	 */
	private OnItemClickListener onListItemClick() {
		OnItemClickListener listener = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				ListEntryItem current = (ListEntryItem) adapter.getItem(position);
				
				if (current.getType().equals("dir")) {
					Log.d("BrowsingActivity", "dir");
					Intent browseFolder = new Intent(getActivity(), BrowsingActivity.class);
					String tmpUrl = serverUrl + "/api/getFolderContent/" + folderId + "?" + current.getUrl();
					browseFolder.putExtra("url", tmpUrl);
					browseFolder.putExtra("foldername", current.getTitle());
					startActivity(browseFolder);
				} else if (current.getType().equals("git")) {
					Log.d("BrowsingActivity", "git");
					Intent browseFolder = new Intent(getActivity(), BrowsingActivity.class);
					folderId = current.getId();
					SharedPreferences prefs = SettingsActivity.getSettings((ContextWrapper) parent.getContext());
					Editor editor = prefs.edit();
					editor.putString("folderId", folderId);
					editor.commit();
					browseFolder.putExtra("url", serverUrl + "/api/getFolderContent/" + folderId);
					browseFolder.putExtra("foldername", current.getTitle());
					startActivity(browseFolder);
				} else if (current.getType().equals("file")) {
					Intent showFile = new Intent(getActivity(), FileDetailsActivity.class);
					showFile.putExtra("ListEntryItem", current);
					showFile.putExtra("ident", ident);
					showFile.putExtra("authCode", authCode);
					showFile.putExtra("serverUrl", serverUrl);
					showFile.putExtra("folderId", folderId);
					startActivity(showFile);
				}
				
			}
		};
		return listener;
	}
	
	private class DownloadFileList extends AsyncTask<String, ListEntryItem, Boolean> {
			
		private boolean isProjectsDirectory = false;
		
		@Override
		protected void onPreExecute() {
		}
		
		private HttpClient getNewHttpClient() {
			SharedPreferences sp = SettingsActivity.getSettings(getActivity());
			boolean acceptAll = sp.getBoolean(getResources().getString(R.string.settings_accept_all_certificates), false);
			
			SchemeRegistry s = new SchemeRegistry();
			s.register(new Scheme("http", new PlainSocketFactory(), 80));
			s.register(new Scheme("https", acceptAll ? new FakeSocketFactory() : SSLSocketFactory.getSocketFactory(), 443));
			
			HttpParams httpParams = new BasicHttpParams();
			return new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, s), httpParams);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			String server = params[0];
			
			try {
				// TODO: Refactor I/O here and in SetupActivity to central place
				HttpClient client = getNewHttpClient();
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
						String type = json.getString("type");
						if (type.equals("git")) {
							isProjectsDirectory = true;
							URI uri = new URI(serverUrl);
							item.setSubtitle(uri.getHost());
						} if (type.equals("file")) {
							item.setFilesize(json.getString("fileSize"));
						}
						
						item.setType(json.getString("type"));
						if (json.has("url")) {
							item.setUrl(json.getString("url"));
						}
						if (json.has("mimeBase")) {
							item.setMimetype(json.getString("mimeBase"));
						}
						if (json.has("mime")) {
							item.setMimetype(json.getString("mime"));
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
			} catch (URISyntaxException e) {
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
			if (isProjectsDirectory) {
				getActivity().setTitle(getString(R.string.projects));
//				addNewActionButton(R.drawable.ic_action_info, R.string.info, new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Intent showAboutScreen = new Intent(context, AboutActivity.class);
//						startActivity(showAboutScreen);
//					}
//
//				});
			} else {
				getActivity().setTitle(foldername);
//				addNewActionButton(R.drawable.ic_action_refresh, R.string.refresh, new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						Intent refreshList = new Intent(context, BrowsingActivity.class);
//						refreshList.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
//						refreshList.putExtra("url", currentUrl);
//						startActivity(refreshList);
//					}
//				});
			}
		}
		
	}
}
