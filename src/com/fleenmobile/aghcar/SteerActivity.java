package com.fleenmobile.aghcar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

/**
 * Main idea is to connect to Raspberry Pi, send there data about thumbs
 * positions (they will be steering a car) and receive video from the Raspberry
 * 
 * @author Rylek
 * 
 */
public class SteerActivity extends Activity {

	private MessageManager manager;

	private VideoView videoView;
	private ImageView leftPaddle;
	private ImageView rightPaddle;

	private Uri video = null;

	private ReceiverTask receiverTask = new ReceiverTask(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_steer);

		// Find views
		videoView = (VideoView) findViewById(R.id.videoView);
		leftPaddle = (ImageView) findViewById(R.id.leftPaddle);
		rightPaddle = (ImageView) findViewById(R.id.rightPaddle);

		manager = new MessageManager(this);

		setViewsAndListeners();
	}

	/** register the BroadcastReceiver with the intent values to be matched */
	@Override
	public void onResume() {
		super.onResume();
		receiverTask = new ReceiverTask(this);
		receiverTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
	}

	@Override
	public void onPause() {
		super.onPause();

		receiverTask.cancel(true);
	}

	private void loadVideo() {
		// Just for testing load video from stream in Internet
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		MediaController mediaController = new MediaController(
				SteerActivity.this);
		mediaController.setAnchorView(videoView);

		video = Uri
				.parse("https://ia700401.us.archive.org/19/items/ksnn_compilation_master_the_internet/ksnn_compilation_master_the_internet_512kb.mp4");

		videoView.setMediaController(mediaController);
		videoView.setVideoURI(video);
		videoView.setOnPreparedListener(new OnPreparedListener() {

			@Override
			public void onPrepared(MediaPlayer arg0) {
				// TODO Auto-generated method stub
				videoView.start();
			}
		});

	}

	/**
	 * Ideally converts video stream to URI so as to able to display it in
	 * videoview
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static String getDataSource(String path) throws IOException {

		if (!URLUtil.isNetworkUrl(path)) {
			return path;
		} else {
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			if (stream == null)
				throw new RuntimeException("stream is null");
			File temp = File.createTempFile("mediaplayertmp", "dat");
			temp.deleteOnExit();
			String tempPath = temp.getAbsolutePath();
			FileOutputStream out = new FileOutputStream(temp);
			byte buf[] = new byte[128];
			do {
				int numread = stream.read(buf);
				if (numread <= 0)
					break;
				out.write(buf, 0, numread);
			} while (true);
			try {
				stream.close();
				out.close();
			} catch (IOException ex) {
				// Log.e(TAG, "error: " + ex.getMessage(), ex);
			}
			return tempPath;
		}
	}

	private void setViewsAndListeners() {
		// Set listener to send info about thumb position to Raspberry Pi
		leftPaddle.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Set listener for removing a finger from paddle (it has to
				// reset mssg)
				if (event.getAction() == MotionEvent.ACTION_UP) {
					manager.setLeftPaddlePosition("-");
					return true;
				}
				// Set the position of finger on the left paddle
				manager.setLeftPaddlePosition(String.valueOf(event.getY()));
				return true;
			}

		});

		rightPaddle.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// Set listener for removing a finger from paddle (it has to
				// reset mssg)
				if (event.getAction() == MotionEvent.ACTION_UP) {
					manager.setRightPaddlePosition("-");
					return true;
				}

				// Set the position of finger on the right paddle
				manager.setRightPaddlePosition(String.valueOf(event.getX()));
				return true;
			}
		});

		// Load video
		// loadVideo();

		leftPaddle.bringToFront();
		rightPaddle.bringToFront();

	}

	/**
	 * Toggles lights in the car
	 * 
	 * @param v
	 *            Lights button
	 */
	public void toggleLights(View v) {
		manager.toggleTheLights();
	}

}
