package org.sparkleshare.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupActivity extends Activity {
	
	private EditText edtServer, edtFolder, edtLinkcode;
	private Button btnSubmit;
	private Context context;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);
        context = this;
        
        edtServer = (EditText) findViewById(R.id.edt_server);
        edtFolder = (EditText) findViewById(R.id.edt_folder_name);
        edtLinkcode = (EditText) findViewById(R.id.edt_link_code);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
    }
    
    /**
     * Will be invoked when submit button was clicked
     * @param target
     */
    public void buttonClick(View target) {
    	switch (target.getId()) {
    	case R.id.btn_submit:
    		new Login().execute();
    	}
    }
    
    /**
     * Login to server asynchronously. 
     * @author kai
     *
     */
    private class Login extends AsyncTask<Void, Void, Boolean> {
    	
    	ProgressDialog loadingDialog;
    	final String AUTH_SUFFIX = "/api/getAuthCode";
    	
    	@Override
    	protected void onPreExecute() {
    		loadingDialog = ProgressDialog.show(context, "", getString(R.string.please_wait));
    	}
    	
    	@Override
    	protected Boolean doInBackground(Void... params) {
    		HttpClient client = new DefaultHttpClient();
    		String address = edtServer.getText().toString();
    		HttpPost post = new HttpPost(address + AUTH_SUFFIX);
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
					editor.commit();
					String exists = prefs.getString("ident", "none");
					Log.d("exists", exists);
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
    			startActivity(browseData);
    		} else {
    			Toast.makeText(context, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
    		}
    	}
    }
}