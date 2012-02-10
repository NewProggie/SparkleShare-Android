package org.sparkleshare.android;

import org.sparkleshare.android.actionbarcompat.ActionBarActivity;
import org.sparkleshare.android.ui.SetupFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * This {@link Activity} handles a new setup for a SparkleShare instance.
 * @author kai
 *
 */
public class SetupActivity extends ActionBarActivity {
	
	private Context context;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    
        setContentView(R.layout.setup_fragment);
        setTitle(getString(R.string.add_project));

        if (getIntent().getStringExtra("url") != null && getIntent().getStringExtra("linkcode") != null) {
        	/* processing scanned QR code */
        	String url = getIntent().getStringExtra("url");
        	String linkcode = getIntent().getStringExtra("linkcode");
        	SetupFragment setupFragment = (SetupFragment) getSupportFragmentManager().findFragmentById(R.id.setup_fragment);
        	setupFragment.edtServer.setText(url);
        	setupFragment.edtLinkcode.setText(linkcode);
        	setupFragment.startLogin(url);
        }
        
    }
    
    /**
     * Will be called when user clicks a button inside this {@link Activity }
     * @param target clicked {@link Button}
     */
    public void buttonClick(View target) {
    	switch (target.getId()) {
    	case R.id.btn_add:
    		SetupFragment setupFragment = (SetupFragment) getSupportFragmentManager().findFragmentById(R.id.setup_fragment);
    		String url = setupFragment.edtServer.getEditableText().toString();
    		setupFragment.startLogin(url);
    		break;
    	case R.id.btn_never_mind:
    		((Activity) context).finish();
    		break;
    	}
    }

}