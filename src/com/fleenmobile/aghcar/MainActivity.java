package com.fleenmobile.aghcar;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static String mIP = "192.168.1.66";
	private String networkName = "Raspberry";
	private String networkPass = "RaspberryPi";
	private static int outPort = 8989;

	public static Socket outSocket;
	public static Socket client;
	private static ServerSocket inSocket;

	private int networkId;
	private volatile boolean connected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Take care of this activity being full screen
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_main);

	}

	public void setSettings(String ip, String networkName, String networkPass) {
		mIP = ip;
		this.networkName = networkName;
		this.networkPass = networkPass;
	}

	/**
	 * Connects or disconnects from raspberry pi
	 * 
	 * @param v
	 *            Layout with connect button
	 */
	public void connect(View v) {
		if (!connected) {
			// Set a connection with WiFi
			setupWiFiConnection();

			// Establish connections with sockets (in and out)
			setupSockets();

		} else {
			// Close sockets
			try {
				outSocket.close();
				inSocket.close();
				connected = false;
			} catch (IOException e) {

			}

			// Disconnect from network
			WifiManager wifiManager = (WifiManager) MainActivity.this
					.getSystemService(WIFI_SERVICE);
			wifiManager.disconnect();

			// Set status to not connected
			setStatus();
		}

	}

	/**
	 * Starts SteerActivity
	 * 
	 * @param v
	 *            Layout with steer button
	 */
	public void steer(View v) {
		if (connected) {
			Intent i = new Intent(this, SteerActivity.class);
			startActivity(i);
		}
	}

	/**
	 * Start SettingsDialog
	 * 
	 * @param v
	 *            Layout with settings button
	 */
	public void settings(View v) {
		SettingsDialog dialog = SettingsDialog.newInstance(mIP, networkName,
				networkPass, this);
		dialog.show(getFragmentManager(), "");
	}

	private void setupWiFiConnection() {
		WifiConfiguration wifiConfig = new WifiConfiguration();
		wifiConfig.SSID = String.format("\"%s\"", networkName);
		wifiConfig.preSharedKey = String.format("\"%s\"", networkPass);

		WifiManager wifiManager = (WifiManager) MainActivity.this
				.getSystemService(WIFI_SERVICE);
		networkId = wifiManager.addNetwork(wifiConfig);
		wifiManager.disconnect();
		wifiManager.enableNetwork(networkId, true);
		wifiManager.reconnect();

		while (wifiManager.getConnectionInfo().getNetworkId() != networkId)
			;

	}

	private void setupSockets() {
		new AsyncTask<Void, Void, Void>() {

			@Override
			protected Void doInBackground(Void... params) {
				try {

					// Wait to WiFi to kick in
					Thread.sleep(10000);

					inSocket = new ServerSocket(outPort);
					inSocket.setReuseAddress(true);
					client = inSocket.accept();

					outSocket = new Socket();
					outSocket.setReuseAddress(true);
					outSocket.bind(null);
					outSocket.connect(new InetSocketAddress(mIP, outPort), 5000);

					connected = true;

					setStatus();
				} catch (Exception e) {
				}

				return null;
			}

		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

	}

	private void setStatus() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (connected) {
					TextView status = (TextView) findViewById(R.id.main_status);
					status.setText(getResources().getString(
							R.string.main_status_connected));
					status.setTextColor(Color.parseColor("#3F7229"));

					// Set button to disconnect
					ImageView connectButton = (ImageView) findViewById(R.id.main_connect_icon);
					connectButton.setBackgroundResource(R.drawable.disconnect);
					TextView connectTextView = (TextView) findViewById(R.id.main_connect_tv);
					connectTextView.setText(getResources().getString(
							R.string.main_disconnect));

				} else {
					TextView status = (TextView) findViewById(R.id.main_status);
					status.setText(getResources().getString(
							R.string.main_status_disconnected));
					status.setTextColor(Color.parseColor("#E10000"));

					// Set button to connect
					ImageView connectButton = (ImageView) findViewById(R.id.main_connect_icon);
					connectButton.setBackgroundResource(R.drawable.connect);
					TextView connectTextView = (TextView) findViewById(R.id.main_connect_tv);
					connectTextView.setText(getResources().getString(
							R.string.main_connect));

				}
			}
		});
	}

	public static String getIp() {
		return mIP;
	}

	public static int getOutPort() {
		return outPort;
	}

}
