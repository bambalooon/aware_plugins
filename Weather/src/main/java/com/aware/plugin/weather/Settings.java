package com.aware.plugin.weather;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import com.aware.Aware;


public class Settings extends Activity {
    public static final String ENABLED_WEATHER_CURRENT = Settings.class.getName() + ".ENABLED_WEATHER_CURRENT";
    public static final String ENABLED_WEATHER_5DAYS = Settings.class.getName() + ".ENABLED_WEATHER_5DAYS";
    public static final String ENABLED_WEATHER_14DAYS = Settings.class.getName() + ".ENABLED_WEATHER_14DAYS";
    public static final String FREQUENCY_WEATHER_CURRENT = Settings.class.getName() + ".FREQUENCY_WEATHER_CURRENT";
    public static final String FREQUENCY_WEATHER_5DAYS = Settings.class.getName() + ".FREQUENCY_WEATHER_5DAYS";
    public static final String FREQUENCY_WEATHER_14DAYS = Settings.class.getName() + ".FREQUENCY_WEATHER_14DAYS";

    public static final int SECOND = 1000;
    public static final int MINUTE = 60;
    public static final int HOUR = 60*MINUTE;
    public static final String TRUE_STATEMENT = "true";
    public static final boolean DEFAULT_ENABLED_CURRENT = true;
    public static final boolean DEFAULT_ENABLED_5DAYS = false;
    public static final boolean DEFAULT_ENABLED_14DAYS = false;
    public static final int DEFAULT_FREQUENCY_CURRENT = HOUR/4;
    public static final int DEFAULT_FREQUENCY_5DAYS = 2*HOUR;
    public static final int DEFAULT_FREQUENCY_14DAYS = 12*HOUR;


    private CheckBox currWthCheckbox;
    private CheckBox _5daysWthCheckbox;
    private CheckBox _14daysWthCheckbox;
    private TextView currWthFq;
    private TextView _5daysWthFq;
    private TextView _14daysWthFq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_ui);

        final ContentResolver contentResolver = getContentResolver();
        loadAwareSettings(contentResolver);

        final Button submitButton = (Button) findViewById(R.id.submit_setting);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currWthFq.getText().length() > 0) {
                    Aware.setSetting(contentResolver, FREQUENCY_WEATHER_CURRENT, currWthFq.getText());
                }
                if(_5daysWthFq.getText().length() > 0) {
                    Aware.setSetting(contentResolver, FREQUENCY_WEATHER_5DAYS, _5daysWthFq.getText());
                }
                if(_14daysWthFq.getText().length() > 0) {
                    Aware.setSetting(contentResolver, FREQUENCY_WEATHER_14DAYS, _14daysWthFq.getText());
                }
                Aware.setSetting(contentResolver, ENABLED_WEATHER_CURRENT, currWthCheckbox.isChecked());
                Aware.setSetting(contentResolver, ENABLED_WEATHER_5DAYS, _5daysWthCheckbox.isChecked());
                Aware.setSetting(contentResolver, ENABLED_WEATHER_14DAYS, _14daysWthCheckbox.isChecked());

                Intent awareRefresh = new Intent(Aware.ACTION_AWARE_REFRESH);
                sendBroadcast(awareRefresh);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAwareSettings(getContentResolver());
    }

    private void loadAwareSettings(ContentResolver contentResolver) {
        currWthCheckbox = (CheckBox) findViewById(R.id.curr_weather_update_enabled);
        final String currWthEnabledText = Aware.getSetting(contentResolver, ENABLED_WEATHER_CURRENT);
        currWthCheckbox.setChecked(currWthEnabledText.length() == 0
                ? DEFAULT_ENABLED_CURRENT
                : TRUE_STATEMENT.equalsIgnoreCase(currWthEnabledText));
        currWthFq = (TextView) findViewById(R.id.curr_weather_update_fq);
        final String currWthFqText = Aware.getSetting(contentResolver, FREQUENCY_WEATHER_CURRENT);
        currWthFq.setText(currWthFqText.length() == 0 ? Integer.toString(DEFAULT_FREQUENCY_CURRENT) : currWthFqText);

        _5daysWthCheckbox = (CheckBox) findViewById(R.id.days5_weather_update_enabled);
        final String _5daysWthEnabledText = Aware.getSetting(contentResolver, ENABLED_WEATHER_5DAYS);
        _5daysWthCheckbox.setChecked(_5daysWthEnabledText.length() == 0
                ? DEFAULT_ENABLED_5DAYS
                : TRUE_STATEMENT.equalsIgnoreCase(_5daysWthEnabledText));
        _5daysWthFq = (TextView) findViewById(R.id.days5_weather_fq_update);
        final String _5daysWthFqText = Aware.getSetting(contentResolver, FREQUENCY_WEATHER_5DAYS);
        _5daysWthFq.setText(_5daysWthFqText.length() == 0 ? Integer.toString(DEFAULT_FREQUENCY_5DAYS) : _5daysWthFqText);

        _14daysWthCheckbox = (CheckBox) findViewById(R.id.days14_weather_update_enabled);
        final String _14daysWthEnabledText = Aware.getSetting(contentResolver, ENABLED_WEATHER_14DAYS);
        _14daysWthCheckbox.setChecked(_14daysWthEnabledText.length() == 0
                ? DEFAULT_ENABLED_14DAYS
                : TRUE_STATEMENT.equalsIgnoreCase(_14daysWthEnabledText));
        _14daysWthFq = (TextView) findViewById(R.id.days14_weather_fq_update);
        final String _14daysWthFqText = Aware.getSetting(contentResolver, FREQUENCY_WEATHER_14DAYS);
        _14daysWthFq.setText(_14daysWthFqText.length() == 0 ? Integer.toString(DEFAULT_FREQUENCY_14DAYS) : _14daysWthFqText);
    }
}
