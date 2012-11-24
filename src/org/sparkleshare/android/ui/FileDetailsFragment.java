package org.sparkleshare.android.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
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
import org.sparkleshare.android.BrowsingActivity;
import org.sparkleshare.android.R;
import org.sparkleshare.android.SettingsActivity;
import org.sparkleshare.android.utils.ExternalDirectory;
import org.sparkleshare.android.utils.FakeSocketFactory;
import org.sparkleshare.android.utils.FormatHelper;
import org.sparkleshare.android.utils.MimetypeChecker;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class FileDetailsFragment extends Fragment {

	private static final String TAG = "FileDetailsFragment";
	private ImageView fileIcon;
	private TextView tvFilename, tvFileSize;
	private Button btnOpenDownloadFile, btnRedownloadFile, btnDeleteFile;
	private ListEntryItem current;
	private File file;
	private String ident, authCode, serverUrl, folderId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.file_details, container, false);
		fileIcon = (ImageView) rl.findViewById(R.id.iv_file_icon);
		tvFilename = (TextView) rl.findViewById(R.id.tv_file_name);
		tvFileSize = (TextView) rl.findViewById(R.id.tv_file_size);
		btnOpenDownloadFile = (Button) rl.findViewById(R.id.btn_toggle_open_download_file);
		btnRedownloadFile = (Button) rl.findViewById(R.id.btn_redownload_file);
		btnDeleteFile = (Button) rl.findViewById(R.id.btn_delete_file);
		
		
		Intent actIntent = getActivity().getIntent();
		current = actIntent.getParcelableExtra("ListEntryItem");
		getActivity().setTitle(current.getTitle());
		ident = actIntent.getStringExtra("ident");
		authCode = actIntent.getStringExtra("authCode");
		serverUrl = actIntent.getStringExtra("serverUrl");
		folderId = actIntent.getStringExtra("folderId");
		file = new File(ExternalDirectory.getExternalRootDirectory() + "/" + current.getTitle());
		if (file.exists()) {
			btnOpenDownloadFile.setText(R.string.open_file);
			btnRedownloadFile.setVisibility(View.VISIBLE);
			btnDeleteFile.setVisibility(View.VISIBLE);
		} else {
			btnOpenDownloadFile.setText(R.string.download_file);
			btnRedownloadFile.setVisibility(View.INVISIBLE);
			btnDeleteFile.setVisibility(View.INVISIBLE);
		}
		fileIcon.setImageResource(MimetypeChecker.getLargeIconforMimetype(current.getMimetype()));
		tvFilename.setText(current.getTitle());
		tvFileSize.setText(FormatHelper.formatFilesize(current.getFilesize()));
		return rl;
	}
	
	public ListEntryItem getCurrentListItem() {
		return current;
	}
	
		
	public void startAsyncFileDownload() {
		File file = new File(ExternalDirectory.getExternalRootDirectory() + "/"	+ current.getTitle());
		if(file.exists() && !file.delete()){
			return;
		}
				
		StringBuilder sb = new StringBuilder();
		sb.append(serverUrl);
		sb.append("/api/getFile/");
		sb.append(folderId + "?");
		sb.append(current.getUrl());
		current.setUrl(sb.toString());
		new DownloadFile().execute(current);
	}
		
	private class DownloadFile extends AsyncTask<ListEntryItem, Integer, Boolean> {

		Notification notification;
		NotificationManager notificationManager;
		private int maxProgress;
		ListEntryItem current;

		@Override
		protected void onPreExecute() {
			notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			Intent notificationIntent = new Intent(getActivity(), BrowsingActivity.class);
			PendingIntent intent = PendingIntent.getActivity(getActivity(), 0, notificationIntent, 0);
			notification = new Notification(R.drawable.ic_stat_download, "", System.currentTimeMillis());
			notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
			notification.contentView = new RemoteViews(getActivity().getApplicationContext().getPackageName(),
					R.layout.download_progress);
			notification.contentIntent = intent;
			
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
		protected Boolean doInBackground(ListEntryItem... params) {
			// TODO: Check for connectivity
			current = params[0];
			try {
				HttpClient client = getNewHttpClient();
				HttpGet get = new HttpGet(current.getUrl());
				get.setHeader("X-SPARKLE-IDENT", ident);
				get.setHeader("X-SPARKLE-AUTH", authCode);
				HttpResponse response = client.execute(get);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					publishProgress(0);
					HttpEntity entity = response.getEntity();
					if (entity != null) {
						notification.contentView.setTextViewText(R.id.tv_download_title, current.getTitle());
						notificationManager.notify(17, notification);
						File file = new File(ExternalDirectory.getExternalRootDirectory() + "/"
								+ current.getTitle());
						maxProgress = Integer.valueOf(current.getFilesize());
						InputStream input = entity.getContent();
						OutputStream output = new FileOutputStream(file);
						byte buffer[] = new byte[1024];
						int count = 0, total = 0;
						long nextUpdate = System.currentTimeMillis() + 1000;
						while ((count = input.read(buffer)) > 0) {
							output.write(buffer, 0, count);
							total += count;
							if (System.currentTimeMillis() > nextUpdate) {
								publishProgress(total);
								nextUpdate = System.currentTimeMillis() + 1000;
							}
						}
						output.flush();
						output.close();
						input.close();
					}
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
			int progress = values[0];
			notification.contentView.setProgressBar(R.id.pb_download_progressbar, maxProgress, progress,
					false);
			notificationManager.notify(17, notification);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			notificationManager.cancel(17);
			if (result) {
				btnOpenDownloadFile.setText(R.string.open_file);
				//btnRedownloadFile.setText(R.string.redownload_file);
				
				btnOpenDownloadFile.setEnabled(true);
				//btnRedownloadFile.setEnabled(true);
				btnRedownloadFile.setVisibility(View.VISIBLE);
				btnDeleteFile.setVisibility(View.VISIBLE);
				
				File file = new File(ExternalDirectory.getExternalRootDirectory() + "/" + current.getTitle());
				String extension = MimeTypeMap.getFileExtensionFromUrl(current.getTitle());

				if (file.exists() && file.length() < 1000000) {
					Bitmap fileBitmap;
					try {
						 BitmapFactory.Options options = new BitmapFactory.Options();
						 options.inSampleSize = 4;
						
						fileBitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
					} catch (Exception e) {
						fileBitmap = null;
					}

					if (fileBitmap != null) {
						fileIcon.setImageBitmap(fileBitmap);
					}
				}
				
			} else {
				Toast.makeText(getActivity(), getString(R.string.downloading_failed), Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void deleteFile() {
		File file = new File(ExternalDirectory.getExternalRootDirectory() + "/"	+ current.getTitle());
		if(file.exists() && file.delete()){
			btnRedownloadFile.setVisibility(View.INVISIBLE);
			btnDeleteFile.setVisibility(View.INVISIBLE);
			btnOpenDownloadFile.setText(R.string.download_file);
		}
	}
	
}
