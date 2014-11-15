package com.fleenmobile.aghcar;

import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class ReceiverTask extends AsyncTask<Void, Void, String> {

	private Context context;

	public ReceiverTask(Context context) {
		this.context = context;
	}

	@Override
	protected String doInBackground(Void... params) {
		try {
			/**
			 * If this code is reached, a client has connected and transferred
			 * String. Display it
			 */
			final StringBuilder sb = new StringBuilder();
			InputStreamReader reader = new InputStreamReader(MainActivity.client.getInputStream());
			final char[] buffer = new char[1024];
			for (;;) {
				final int rsz = reader.read(buffer, 0, buffer.length);
				if (rsz > 0) {
					((SteerActivity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							Toast.makeText(context, sb.append(buffer, 0 ,rsz).toString(),
									Toast.LENGTH_SHORT).show();
							sb.delete(0, sb.length());
						}
					});
				}
			}

		} catch (IOException e) {
			return null;
		}
	}

}
