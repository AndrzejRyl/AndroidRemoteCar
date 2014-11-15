package com.fleenmobile.aghcar;

import java.io.IOException;
import java.io.OutputStreamWriter;

import android.os.AsyncTask;

public class SenderTask extends AsyncTask<Void, Void, Void> {

	private static OutputStreamWriter writer;

	private String mssg;

	public SenderTask(String mssg) {
		super();
		this.mssg = mssg;
		
		try {
		if (writer == null)
			writer = new OutputStreamWriter(MainActivity.outSocket.getOutputStream());
		} catch (IOException e) {
		}
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		try {
			/**
			 * Send mssg
			 */
			
			writer.write(mssg);
			writer.flush();

			// !!! DATAGRAM JUST IN CASE !!!

			// send message to Pi
			/*
			 * InetAddress serverAddr = InetAddress.getByName(HOST);
			 * Log.e("das","das"); DatagramSocket clientSocket = new
			 * DatagramSocket(); Log.e("cxzz","Zonk"); byte[] sendData = new
			 * byte[1024]; String sentence = mssg; sendData =
			 * sentence.getBytes(); DatagramPacket sendPacket = new
			 * DatagramPacket(sendData, sendData.length, serverAddr, 8988);
			 * clientSocket.send(sendPacket);
			 */
			// get reply back from Pi
			// byte[] receiveData1 = new byte[1024];
			// DatagramPacket receivePacket = new DatagramPacket(receiveData1,
			// receiveData1.length);
			// clientSocket.receive(receivePacket);

			// clientSocket.close();

		} catch (IOException e) {
			// catch logic
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		MessageManager.setSenderRunning(false);

	}

}
