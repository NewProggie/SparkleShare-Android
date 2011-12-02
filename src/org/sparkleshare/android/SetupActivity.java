package org.sparkleshare.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;
import org.sparkleshare.android.ui.BaseActivity;
import org.transdroid.util.FakeSocketFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


/**
 * This {@link Activity} handles a new setup for a SparkleShare instance.
 * @author kai
 *
 */
public class SetupActivity extends BaseActivity {
	
	private EditText edtServer, edtLinkcode;
	private Context context;
	private Button btn_add;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.setup);
        
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setEnabled(false);
        edtServer = (EditText) findViewById(R.id.edt_server);
        edtLinkcode = (EditText) findViewById(R.id.edt_link_code);
        
        edtServer.addTextChangedListener(checkEditfields());
        edtLinkcode.addTextChangedListener(checkEditfields());
        
        setupActionBarWithoutHomeButton(getString(R.string.add_project), Color.BLACK);

        if (getIntent().getStringExtra("url") != null && getIntent().getStringExtra("linkcode") != null) {
        	/* processing scanned QR code */
        	String url = getIntent().getStringExtra("url");
        	String linkcode = getIntent().getStringExtra("linkcode");
        	edtServer.setText(url);
        	edtLinkcode.setText(linkcode);
        	new Login().execute(url);
        }
        
    }
    
    /**
     * Will be called when user clicks a button inside this {@link Activity }
     * @param target clicked {@link Button}
     */
    public void buttonClick(View target) {
    	switch (target.getId()) {
    	case R.id.btn_add:
    		String url = edtServer.getEditableText().toString();
    		new Login().execute(formatServerUrl(url));
    		break;
    	case R.id.btn_never_mind:
    		((Activity) context).finish();
    		break;
    	}
    }
    
    private String formatServerUrl(String url) {
    	// TODO: Should use regular expression here.
    	if (!url.contains(":")) {
    		url = url.concat(":3000");
    	}
    	if (!url.startsWith("http")) {
    		url = "http://" + url;
    	}
    	return url;
    }
    
    /**
     * Checks both mandatory edit fields and enables the add button inside this Activity.
     * @return
     */
    private TextWatcher checkEditfields() {
    	TextWatcher watcher = new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (edtServer.getEditableText().length() > 0 && edtLinkcode.getEditableText().length() > 0) {
					btn_add.setEnabled(true);
				} else {
					btn_add.setEnabled(false);
				}
			}
		};
		return watcher;
    }
    
    /**
     * Login to server asynchronously. 
     * @author kai
     *
     */
    public class Login extends AsyncTask<String, Void, Boolean> {
    	
    	private ProgressDialog loadingDialog;
    	private final String AUTH_SUFFIX = "/api/getAuthCode";
    	private String serverUrl;
    	
    	@Override
    	protected void onPreExecute() {
    		loadingDialog = ProgressDialog.show(context, "", getString(R.string.adding_project));
    	}
    	
    	private HttpClient getNewHttpClient() {
    		SharedPreferences sp = SettingsActivity.getSettings(SetupActivity.this);
    		boolean acceptAll = sp.getBoolean(getResources().getString(R.string.settings_accept_all_certificates), false);
    		
    		SchemeRegistry s = new SchemeRegistry();
    		s.register(new Scheme("http", new PlainSocketFactory(), 80));
    		s.register(new Scheme("https", acceptAll ? new FakeSocketFactory() : SSLSocketFactory.getSocketFactory(), 443));
    		
    		HttpParams httpParams = new BasicHttpParams();
    		
    		return new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, s), httpParams);
    	}
    	
    	@Override
    	protected Boolean doInBackground(String... params) {
    		HttpClient client = getNewHttpClient();
    		serverUrl = params[0];
    		HttpPost post = new HttpPost(serverUrl + AUTH_SUFFIX);
    		try {
    			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        		nameValuePairs.add(new BasicNameValuePair("code", edtLinkcode.getText().toString()));
        		nameValuePairs.add(new BasicNameValuePair("name", android.os.Build.MODEL));
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = client.execute(post);
				if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					StringBuffer sb = new StringBuffer();
					String line = "";
					String NL = System.getProperty("line.separator");
					while ((line = in.readLine()) != null) {
						sb.append(line + NL);
					}
					in.close();
					JSONObject credentials = new JSONObject(sb.toString());
					// TODO: encrypt credentials 
					String ident = credentials.getString("ident");
					String authCode = credentials.getString("authCode");
					SharedPreferences prefs = SettingsActivity.getSettings((ContextWrapper) context);
					Editor editor  = prefs.edit();
					editor.putString("ident", ident);
					editor.putString("authCode", authCode);
					editor.putString("serverUrl", serverUrl);
					editor.commit();
				}
			} catch (UnsupportedEncodingException e) {
				Log.e("Login failed", e.getLocalizedMessage());
				return false;
			} catch (ClientProtocolException e) {
				Log.e("Login failed", e.getLocalizedMessage());
				return false;
			} catch (IOException e) {
				Log.e("Login failed", e.getLocalizedMessage());
				return false;
			} catch (JSONException e) {
				Log.e("Login failed", e.getLocalizedMessage());
				return false;
			} catch (Exception e) {
				Log.e("Login failed", e.getLocalizedMessage());
				return false;
			}
    		return true;
    	}
    	
    	@Override
    	protected void onPostExecute(Boolean successfully) {
    		loadingDialog.dismiss();
    		if (successfully) {
    			Intent browseData = new Intent(context, BrowsingActivity.class);
    			// TODO: outsource API constants
    			browseData.putExtra("url", serverUrl + "/api/getFolderList");
    			startActivity(browseData);
    		} else {
    			AlertDialog.Builder builder = new AlertDialog.Builder(context);
    			builder.setMessage(getString(R.string.login_error))
    				.setCancelable(false)
    				.setPositiveButton(getString(R.string.close), new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							((Activity)context).finish();
						}
					});
    			AlertDialog alert = builder.create();
    			alert.show();
    		}
    	}
    }
}