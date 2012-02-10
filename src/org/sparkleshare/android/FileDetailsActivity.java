package org.sparkleshare.android;

import java.io.File;

import org.sparkleshare.android.actionbarcompat.ActionBarActivity;
import org.sparkleshare.android.ui.FileDetailsFragment;
import org.sparkleshare.android.ui.ListEntryItem;
import org.sparkleshare.android.utils.ExternalDirectory;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FileDetailsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filedownload_fragment);
	}

	public void buttonClick(View target) {
		Button btn = (Button) target;
		String text = btn.getText().toString();
		FileDetailsFragment fdFragment = (FileDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.filedetails_fragment);
		ListEntryItem current = fdFragment.getCurrentListItem();
		File file = new File(ExternalDirectory.getExternalRootDirectory() + "/" + current.getTitle());
		if (text.equals(getString(R.string.open_file))) {
			Intent open = new Intent(Intent.ACTION_VIEW, Uri.parse(file.getAbsolutePath()));
			open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			open.setAction(android.content.Intent.ACTION_VIEW);
			open.setDataAndType((Uri.fromFile(file)), current.getMimetype());
			try {
				startActivity(open);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, getString(R.string.activity_not_found), Toast.LENGTH_SHORT).show();
			}
		} else {
			/* text.equals(R.string.download_file) */
			btn.setText(getString(R.string.downloading));
			btn.setEnabled(false);
			fdFragment.startAsyncFileDownload();
		}
	}


}
