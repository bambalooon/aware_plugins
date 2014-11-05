/**
@author: denzil
 */

package com.aware.plugin.google.activityrecognition;

import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.google.activityrecognition.Google_AR_Provider.Google_Activity_Recognition_Data;
import com.aware.utils.Converters;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.util.Log;

public class Algorithm extends IntentService {

    private boolean DEBUG = false;
    private String TAG = "AWARE::Google AR";

    public Algorithm() {
        super("AWARE::Google Activity Recognition");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        DEBUG = Aware.getSetting(getContentResolver(), Aware_Preferences.DEBUG_FLAG).equals("true");
        TAG = Aware.getSetting(getContentResolver(), Aware_Preferences.DEBUG_TAG).length() > 0 ? Aware
                .getSetting(getContentResolver(), Aware_Preferences.DEBUG_TAG) : TAG;

        if (ActivityRecognitionResult.hasResult(intent)) {
            
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);

            DetectedActivity mostProbable = result.getMostProbableActivity();
            
            JSONArray activities = new JSONArray();
            List<DetectedActivity> otherActivities = result.getProbableActivities();
            for( int i=0; i< otherActivities.size(); i++ ){
                DetectedActivity activity = otherActivities.get(i);
                
                try {
                    JSONObject item = new JSONObject();
                    item.put("activity", getActivityName(activity.getType()));
                    item.put("confidence", activity.getConfidence());
                    activities.put(item);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Plugin.current_confidence = mostProbable.getConfidence();
            Plugin.current_activity = mostProbable.getType();
            String activity_name = getActivityName(Plugin.current_activity);

            Intent context = new Intent( Plugin.ACTION_AWARE_GOOGLE_ACTIVITY_RECOGNITION );
            context.putExtra( Plugin.EXTRA_ACTIVITY, activity_name);
            context.putExtra( Plugin.EXTRA_CONFIDENCE, Plugin.current_confidence );
            sendBroadcast( context );

            ContentValues data = new ContentValues();
            data.put(Google_Activity_Recognition_Data.TIMESTAMP, System.currentTimeMillis());
            data.put(Google_Activity_Recognition_Data.DEVICE_ID, Aware.getSetting(getContentResolver(), Aware_Preferences.DEVICE_ID));
            data.put(Google_Activity_Recognition_Data.ACTIVITY_NAME, activity_name);
            data.put(Google_Activity_Recognition_Data.ACTIVITY_TYPE, Plugin.current_activity);
            data.put(Google_Activity_Recognition_Data.CONFIDENCE, Plugin.current_confidence);
            data.put(Google_Activity_Recognition_Data.ACTIVITIES, activities.toString());

            getContentResolver().insert(Google_Activity_Recognition_Data.CONTENT_URI, data);

            if (DEBUG) {
            	Log.d(TAG, "User is: " + activity_name + " (conf:" + Plugin.current_confidence + ")");
            }
        }
    }

    private String getActivityName(int type) {
        switch (type) {
            case DetectedActivity.IN_VEHICLE:
                return "in_vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "on_bicycle";
            case DetectedActivity.ON_FOOT:
                return "on_foot";
            case DetectedActivity.STILL:
                return "still";
            case DetectedActivity.UNKNOWN:
                return "unknown";
            case DetectedActivity.TILTING:
                return "tilting";
            default:
                return "unknown";
        }
    }
}
