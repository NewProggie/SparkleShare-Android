package org.sparkleshare.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.CharBuffer;

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
import org.sparkleshare.android.ui.BaseActivity;
import org.sparkleshare.android.ui.FormatHelper;
import org.sparkleshare.android.ui.ListEntryItem;
import org.sparkleshare.android.utils.ExternalDirectory;
import org.sparkleshare.android.utils.MimetypeChecker;
import org.transdroid.util.FakeSocketFactory;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

public class FileDetailsActivity extends BaseActivity {

	private Context context;
	private ImageView fileIcon;
	private TextView tvFilename, tvFileSize;
	private Button btnOpenDownloadFile;
	private ListEntryItem current;
	private File file;
	private String ident, authCode, serverUrl, folderId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.file_details);

		context = this;
		fileIcon = (ImageView) findViewById(R.id.iv_file_icon);
		tvFilename = (TextView) findViewById(R.id.tv_file_name);
		tvFileSize = (TextView) findViewById(R.id.tv_file_size);
		btnOpenDownloadFile = (Button) findViewById(R.id.btn_toggle_open_download_file);

		current = getIntent().getParcelableExtra("ListEntryItem");
		ident = getIntent().getStringExtra("ident");
		authCode = getIntent().getStringExtra("authCode");
		serverUrl = getIntent().getStringExtra("serverUrl");
		folderId = getIntent().getStringExtra("folderId");
		file = new File(ExternalDirectory.getExternalRootDirectory() + "/" + current.getTitle());
		if (file.exists() &&
				compareHash()) {
			btnOpenDownloadFile.setText(R.string.open_file);
		} else {
			btnOpenDownloadFile.setText(R.string.download_file);
		}

		setupActionBar(current.getTitle(), Color.WHITE);
		fileIcon.setImageResource(MimetypeChecker.getLargeIconforMimetype(current.getMimetype()));
		tvFilename.setText(current.getTitle());
		tvFileSize.setText(FormatHelper.formatFilesize(current.getFilesize()));
	}
	
	private boolean compareHash() {
		File f = new File(ExternalDirectory.getExternalRootDirectory() + "/" + current.getTitle() + ".hash");
		try {
			char[] hash = new char[42];
			FileInputStream in = new FileInputStream(f);
			InputStreamReader r = new InputStreamReader(in);
			
			r.read(hash);
			
			r.close();
			
			String sHash = new String(hash).trim();
			
			return sHash.equals(current.getHash());
			
		} catch (IOException e) {
			return true; // ???
		}
	}

	public void buttonClick(View target) {
		Button btn = (Button) target;
		String text = btn.getText().toString();
		if (text.equals(getString(R.string.open_file))) {
			Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getAbsolutePath()));
			open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			open.setAction(android.content.Intent.ACTION_VIEW);
			open.setDataAndType((Uri.fromFile(file)), current.getMimetype());
			
			try {
				startActivity(open);
			} catch(ActivityNotFoundException e) {
				Toast.makeText(context, getString(R.string.activity_not_found), Toast.LENGTH_SHORT).show();
			}
		} else {
			/* text.equals(R.string.download_file) */
			btn.setText(getString(R.string.downloading));
			btn.setEnabled(false);
			StringBuilder sb = new StringBuilder();
			sb.append(serverUrl);
			sb.append("/api/getFile/");
			sb.append(folderId + "?");
			sb.append(current.getUrl());
			current.setUrl(sb.toString());
			new DownloadFile().execute(current);
		}
	}

	private class DownloadFile extends AsyncTask<ListEntryItem, Integer, Boolean> {

		Notification notification;
		NotificationManager notificationManager;
		private int maxProgress;
		ListEntryItem current;

		@Override
		protected void onPreExecute() {
			notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
			Intent notificationIntent = new Intent(context, BrowsingActivity.class);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			notification = new Notification(R.drawable.ic_stat_download, "", System.currentTimeMillis());
			notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
			notification.contentView = new RemoteViews(getApplicationContext().getPackageName(),
					R.layout.download_progress);
			notification.contentIntent = intent;
			
		}
		
		private HttpClient getNewHttpClient() {
    		SharedPreferences sp = SettingsActivity.getSettings(FileDetailsActivity.this);
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
			
			File hashFile = new File(ExternalDirectory.getExternalRootDirectory() + "/"
					+ current.getTitle() + ".hash");
			Writer w;
			
			try {
				w = new FileWriter(hashFile);
				w.write(current.getHash());
				
				w.flush();
				w.close();
			} catch(IOException e) {
				Log.e("DownloadFile", "Could not write hash file: " + e.getLocalizedMessage());
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
				btnOpenDownloadFile.setEnabled(true);
			} else {
				Toast.makeText(context, getString(R.string.downloading_failed), Toast.LENGTH_SHORT).show();
			}
		}
	}
}
