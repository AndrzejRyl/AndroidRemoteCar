package com.fleenmobile.aghcar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

/**
 * A dialog for settings
 * 
 * @author Rylek
 * 
 */
public class SettingsDialog extends DialogFragment {

	private static MainActivity creatorInstance;

	private static EditText ipView;
	private static EditText networkNameView;
	private static EditText networkPassView;

	private static String ip;
	private static String networkName;
	private static String networkPass;

	private static View view;

	public static SettingsDialog newInstance(String ipAddress, String name,
			String pass, MainActivity instance) {

		// Start a dialog
		SettingsDialog dialog = new SettingsDialog();

		// Get a holder to host activity
		creatorInstance = instance;

		ip = ipAddress;
		networkName = name;
		networkPass = pass;

		return dialog;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
				.setPositiveButton(R.string.ok,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {

								// Set settings
								setSettings();
								dismiss();
							}
						}).setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dismiss();
							}
						});

		builder.setView(view = getActivity().getLayoutInflater().inflate(
				R.layout.fragment_settings_dialog, null));

		// Find views
		ipView = (EditText) view.findViewById(R.id.settings_ip_edit);
		networkNameView = (EditText) view
				.findViewById(R.id.settings_network_name_edit);
		networkPassView = (EditText) view
				.findViewById(R.id.settings_network_pass_edit);

		return builder.create();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		super.onActivityCreated(savedInstanceState);

		// Fill editTexts
		ipView.setText(ip);
		networkNameView.setText(networkName);
		networkPassView.setText(networkPass);
	}

	private void setSettings() {
		ip = ipView.getText().toString();
		networkName = networkNameView.getText().toString();
		networkPass = networkPassView.getText().toString();
		
		creatorInstance.setSettings(ip, networkName, networkPass);
	}

}