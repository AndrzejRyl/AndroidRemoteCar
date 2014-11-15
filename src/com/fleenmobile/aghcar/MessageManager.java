package com.fleenmobile.aghcar;

import android.content.Context;
import android.os.AsyncTask;

/**
 * This class builds and sends mssgs to RaspberryPi
 * 
 * @author Rylek
 * 
 */
public class MessageManager {

	private String leftPaddleMssg = "-";
	private String rightPaddleMssg = "-";
	private String acceleration = "-";
	private volatile static boolean senderRunning = false;
	private String mssg;
	private String lastMssg = "";
	private Context context;
	private SenderTask sender;

	public MessageManager(Context context) {
		this.context = context;
	}

	public void toggleTheLights() {
		sendMssg("lights");
	}

	private void sendMssg(String mssg) {
		if (!senderRunning) {
			sender = new SenderTask(mssg);
			sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			senderRunning = true;
		}
	}

	private void sendMssg() {
		mssg = leftPaddleMssg + rightPaddleMssg + acceleration;

		// Send only if sth has changed
		if (!lastMssg.equals(mssg)) {
			lastMssg = mssg;
			if (!senderRunning) {
				sender = new SenderTask(mssg);
				sender.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
				senderRunning = true;
			}
		}
	}

	public static void setSenderRunning(boolean running) {
		senderRunning = running;
	}

	/**
	 * Sets a mssg to raspberry pi build from left paddle position
	 * 
	 * @param leftPaddlePosition
	 */
	public synchronized void setLeftPaddlePosition(String leftPaddlePosition) {
		if (leftPaddlePosition.equals("-")) {
			leftPaddleMssg = "-";
			acceleration = "-";
			sendMssg();
			return;
		}

		setAcceleration(leftPaddlePosition);
		float paddle = Float.valueOf(leftPaddlePosition);
		float size = context.getResources().getDimension(R.dimen.paddle_size);

		// Determine whether user wants to go forwards/backwards or he wants to
		// stop
		if (paddle < size / 3)
			leftPaddleMssg = "F";
		else if (paddle > 2 * size / 3)
			leftPaddleMssg = "B";
		else
			leftPaddleMssg = "-";

		sendMssg();
	}

	/**
	 * Sets a mssg to raspberry pi build from right paddle position
	 * 
	 * @param rightPaddlePosition
	 */
	public synchronized void setRightPaddlePosition(String rightPaddlePosition) {
		if (rightPaddlePosition.equals("-")) {
			rightPaddleMssg = "-";
			sendMssg();
			return;
		}

		float paddle = Float.valueOf(rightPaddlePosition);
		float size = context.getResources().getDimension(R.dimen.paddle_size);

		// Determine whether user wants to go left/right/straight
		if (paddle < size / 3)
			rightPaddleMssg = "L";
		else if (paddle > 2 * size / 3)
			rightPaddleMssg = "R";
		else
			rightPaddleMssg = "-";

		sendMssg();
	}

	/**
	 * Sets a mssg to raspberry pi build from left paddle position
	 * 
	 * @param leftPaddlePosition
	 */
	private synchronized void setAcceleration(String leftPaddlePosition) {
		float paddle = Float.valueOf(leftPaddlePosition);
		float size = context.getResources().getDimension(R.dimen.paddle_size);

		// Determine acceleration
		if ((paddle < size / 9) || (paddle > 8 * size / 9))
			acceleration = "3";
		else if ((paddle < 2 * size / 9) || (paddle > 7 * size / 9))
			acceleration = "2";
		else if ((paddle < size / 3) || (paddle > 2 * size / 3))
			acceleration = "1";
		else
			acceleration = "-";
	}
}
