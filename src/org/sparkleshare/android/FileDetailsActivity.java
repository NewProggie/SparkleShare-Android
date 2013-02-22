package org.sparkleshare.android;

import java.io.File;

import org.sparkleshare.android.actionbarcompat.ActionBarActivity;
import org.sparkleshare.android.ui.FileDetailsFragment;
import org.sparkleshare.android.ui.ListEntryItem;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class FileDetailsActivity extends ActionBarActivity {

	private Button btnOpenDownloadFile, btnRedownloadFile, btnDeleteFile;
	private ImageView ivFileIcon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filedownload_fragment);

		btnOpenDownloadFile = (Button) findViewById(R.id.btn_toggle_open_download_file);
		btnRedownloadFile = (Button) findViewById(R.id.btn_redownload_file);
		btnDeleteFile = (Button) findViewById(R.id.btn_delete_file);

		ivFileIcon = (ImageView) findViewById(R.id.iv_file_icon);

		FileDetailsFragment fdFragment = (FileDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.filedetails_fragment);
		ListEntryItem current = fdFragment.getCurrentListItem();
		File file = new File(current.getFilePath());
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
				ivFileIcon.setImageBitmap(fileBitmap);
			}
		}
	}

	public void buttonClick(View target) {
		Button btn = (Button) target;
		String text = btn.getText().toString();
		FileDetailsFragment fdFragment = (FileDetailsFragment) getSupportFragmentManager().findFragmentById(R.id.filedetails_fragment);
		ListEntryItem current = fdFragment.getCurrentListItem();
		File file = new File(current.getFilePath());
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
		} else if (text.equals(getString(R.string.delete_file))) {
			fdFragment.deleteFile();
		} else {
			/* text.equals(R.string.download_file) */
			/*
			 * btn.setText(getString(R.string.downloading)); btn.setEnabled(false);
			 */

			btnOpenDownloadFile.setText(getString(R.string.downloading));
			// btnRedownloadFile.setText(getString(R.string.downloading));

			btnRedownloadFile.setVisibility(View.INVISIBLE);
			btnDeleteFile.setVisibility(View.INVISIBLE);

			btnOpenDownloadFile.setEnabled(false);
			/*
			 * btnRedownloadFile.setEnabled(false); btnDeleteFile.setEnabled(false);
			 */

			fdFragment.startAsyncFileDownload();
		}
	}

}
