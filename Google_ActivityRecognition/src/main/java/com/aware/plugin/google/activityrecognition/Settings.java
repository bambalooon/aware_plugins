/**
@author: denzil
*/
package com.aware.plugin.google.activityrecognition;

import com.aware.Aware;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends Activity {
	
	/**
	 * State of Google's Activity Recognition plugin
	 */
	public static final String STATUS_GOOGLE_ACTIVITY_RECOGNITION = "status_google_activity_recognition";
	
	/**
	 * Frequency of Google's Activity Recognition plugin in seconds<br/>
	 * By default = 60 seconds
	 */
	public static final String FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION = "frequency_google_activity_recognition";
	
	/**
	 * Update frequency for Google Activity Recognition in seconds ( default = 60 seconds)
	 */
	public static int frequency = 60; 
	
	private static EditText frequencyText = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings_ui);
		
		frequencyText = (EditText) findViewById(R.id.frequency_update);
		frequencyText.setText(Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION));
		
		Button submit = (Button) findViewById(R.id.submit_setting);
		submit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if( frequencyText.getText().length() > 0 )
					Aware.setSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION, frequencyText.getText());
				
				Intent aware = new Intent(Aware.ACTION_AWARE_REFRESH);
				sendBroadcast(aware);
				finish();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		frequencyText.setText(Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_ACTIVITY_RECOGNITION));
	}
}
