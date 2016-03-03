package polohalo.ua.locationfence.service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import polohalo.ua.locationfence.activity.BlockedActivity;
import polohalo.ua.locationfence.utils.AppsManager;

/**
 * Created by mac on 2/24/16.
 */
/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * Listens for geofence transition changes.
 */
public class GeofenceService extends IntentService {

    private static final String TAG = "GeofenceIntentService";
    private GoogleApiClient mGoogleApiClient;

    public GeofenceService() {
        super("service-tag");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate, here");
    }

    @Override
    protected void onHandleIntent(Intent intent) {//todo stop stop stop ws should start annoying service to monitor foreground app all the time, not like this
        Log.e(TAG, "onHandleIntent");
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
            Log.e(TAG, "Location Services error: " + errorCode);
        } else {
            String triggeredGeoFenceId = geoFenceEvent.getTriggeringGeofences().get(0).getRequestId();
            int transitionType = geoFenceEvent.getGeofenceTransition();
            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                Log.e(TAG, "entering geofence");
                if(AppsManager.blacklistContains(triggeredGeoFenceId)) {
                    Log.e(TAG, "it contains!");
                    if (AppsManager.foregroundTaskIsBlocked(AppsManager.printForegroundTask(this))) {
                        startBlockedActivity(AppsManager.printForegroundTask(this));
                        Log.e(TAG, "blocked this app");
                    }
                }
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                Log.e(TAG,"exiting geofence");
            }
            Log.e(TAG, "with id = " + triggeredGeoFenceId);
        }
    }

    private void startBlockedActivity(String taskName){
        Intent dialogIntent = new Intent(this, BlockedActivity.class);
        dialogIntent.putExtra("taskName", taskName);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }


}
