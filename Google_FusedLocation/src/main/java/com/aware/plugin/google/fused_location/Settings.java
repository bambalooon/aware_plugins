/**
@author: denzil
*/
package com.aware.plugin.google.fused_location;

import com.aware.Aware;
import com.google.android.gms.location.LocationRequest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class Settings extends Activity {

    public static final String STATUS_GOOGLE_FUSED_LOCATION = "status_google_fused_location";
    public static final String FREQUENCY_GOOGLE_FUSED_LOCATION = "frequency_google_fused_location";
    public static final String MAX_FREQUENCY_GOOGLE_FUSED_LOCATION = "max_frequency_google_fused_location";
    public static final String ACCURACY_GOOGLE_FUSED_LOCATION = "accuracy_google_fused_location";
    
    /**
     * Update interval for location, in seconds ( default = 180 )
     */
    public static int update_interval = 180;
    
    /**
     * Fastest update interval for location, in seconds ( default = 1 )
     */
    public static int max_update_interval = 1;
    
    /**
     * Desired location accuracy (default = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY <br/>
     * Other possible Values: {@link LocationRequest#PRIORITY_HIGH_ACCURACY} or<br/>
     * {@link LocationRequest#PRIORITY_NO_POWER}
     */
    public static int location_accuracy = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
    
    private static EditText update_frequency = null;
    private static EditText max_update_frequency = null;
    private static Spinner accuracy_level = null;
    private static ArrayAdapter<CharSequence> adapter = null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.settings);
        
        update_frequency = (EditText) findViewById(R.id.update_frequency);
        update_frequency.setText(Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION));
        
        max_update_frequency = (EditText) findViewById(R.id.fastest_update_frequency);
        max_update_frequency.setText(Aware.getSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION));
        
        accuracy_level = (Spinner) findViewById(R.id.accuracy_level);
        adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.accuracies, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accuracy_level.setAdapter(adapter);
        
        if( Integer.parseInt(Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION)) == LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY ) {
            accuracy_level.setSelection(0);
            location_accuracy = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        } else if ( Integer.parseInt(Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION)) == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            accuracy_level.setSelection(1);
            location_accuracy = LocationRequest.PRIORITY_HIGH_ACCURACY;
        } else if( Integer.parseInt(Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION)) == LocationRequest.PRIORITY_NO_POWER) {
            accuracy_level.setSelection(2);
            location_accuracy = LocationRequest.PRIORITY_NO_POWER;
        } else {
            accuracy_level.setSelection(0);
            location_accuracy = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        }
        
        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( update_frequency.getText().length() > 0 ) {
                    Aware.setSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION, update_frequency.getText());
                }
                if( max_update_frequency.getText().length() > 0) {
                    Aware.setSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION, max_update_frequency.getText());
                }
                switch( accuracy_level.getSelectedItemPosition() ) {
                    case 0:
                        Aware.setSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                        break;
                    case 1:
                        Aware.setSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION, LocationRequest.PRIORITY_HIGH_ACCURACY);
                        break;
                    case 2:
                        Aware.setSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION, LocationRequest.PRIORITY_NO_POWER);
                        break;
                }
                
                Intent aware = new Intent(Aware.ACTION_AWARE_REFRESH);
                sendBroadcast(aware);
                finish();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        update_frequency.setText(Aware.getSetting(getContentResolver(), Settings.FREQUENCY_GOOGLE_FUSED_LOCATION));
        max_update_frequency.setText(Aware.getSetting(getContentResolver(), Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION));
        
        if( Integer.parseInt(Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION)) == LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY ) {
            accuracy_level.setSelection(0);
            location_accuracy = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        } else if ( Integer.parseInt(Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION)) == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            accuracy_level.setSelection(1);
            location_accuracy = LocationRequest.PRIORITY_HIGH_ACCURACY;
        } else if( Integer.parseInt(Aware.getSetting(getContentResolver(), Settings.ACCURACY_GOOGLE_FUSED_LOCATION)) == LocationRequest.PRIORITY_NO_POWER) {
            accuracy_level.setSelection(2);
            location_accuracy = LocationRequest.PRIORITY_NO_POWER;
        } else {
            accuracy_level.setSelection(0);
            location_accuracy = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        }
    }
}
