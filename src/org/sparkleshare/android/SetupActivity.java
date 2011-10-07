package org.sparkleshare.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.sparkleshare.android.ui.BaseActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupActivity extends BaseActivity {
	
	private EditText edtServer, edtLinkcode;
	private Context context;
	private Button btn_add;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.setup);
        
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_add.setEnabled(false);
        edtServer = (EditText) findViewById(R.id.edt_server);
        edtLinkcode = (EditText) findViewById(R.id.edt_link_code);
        
        setupActionBarWithoutHomeButton(getString(R.string.add_project), Color.BLACK);

        if (getIntent().getStringExtra("url") != null) {
        	/* processing scanned QR code */
        	String url = getIntent().getStringExtra("url");
        	String linkcode = getIntent().getStringExtra("linkcode");
        	edtServer.setText(url);
        	edtLinkcode.setText(linkcode);
        	new Login().execute(url);
        }
        
    }
    
    /**
     * Will be invoked when submit button was clicked
     * @param target
     */
    public void buttonClick(View target) {
    	switch (target.getId()) {
    	case R.id.btn_add:
    		new Login().execute(edtServer.getEditableText().toString());
    		break;
    	case R.id.btn_never_mind:
    		((Activity) context).finish();
    		break;
    	}
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
    		loadingDialog = ProgressDialog.show(context, "", getString(R.string.please_wait));
    	}
    	
    	@Override
    	protected Boolean doInBackground(String... params) {
    		HttpClient client = new DefaultHttpClient();
    		serverUrl = params[0];
    		HttpPost post = new HttpPost(serverUrl + AUTH_SUFFIX);
    		try {
    			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        		nameValuePairs.add(new BasicNameValuePair("code", edtLinkcode.getText().toString()));
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
    			Toast.makeText(context, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
    		}
    	}
    }
}