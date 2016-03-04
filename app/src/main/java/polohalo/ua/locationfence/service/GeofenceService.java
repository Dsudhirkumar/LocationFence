package polohalo.ua.locationfence.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

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
    HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
    Handler handler;
    private long triggeredGeoFenceId;
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.e("Handlers", "Called on  customThread");
            if (AppsManager.blacklistContains(triggeredGeoFenceId)) {
                Log.e(TAG, "it contains!");
                if (AppsManager.foregroundTaskIsBlocked(AppsManager.printForegroundTask(GeofenceService.this))) {
                    startBlockedActivity(AppsManager.printForegroundTask(GeofenceService.this));
                    Log.e(TAG, "blocked this app");
                }
            }
            handler.postDelayed(runnableCode, 10000);
        }
    };

    public GeofenceService() {
        super("service-tag");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate, here");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
        Log.e(TAG, "onDestroy, here");
    }

    @Override
    protected void onHandleIntent(Intent intent) {//todo stop stop stop ws should start annoying service to monitor foreground app all the time, not like this
        Log.e(TAG, "onHandleIntent");
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if (geoFenceEvent.hasError()||geoFenceEvent.getTriggeringGeofences()==null) {
            //int errorCode = geoFenceEvent.getErrorCode();
            Log.e(TAG, "Location Services error");
            stopRepeatingService(triggeredGeoFenceId);
        } else {
            triggeredGeoFenceId =Long.valueOf(geoFenceEvent.getTriggeringGeofences().get(0).getRequestId());
            int transitionType = geoFenceEvent.getGeofenceTransition();
            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                Log.e(TAG, "entering geofence");
                startRepeatingService(triggeredGeoFenceId);
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                Log.e(TAG, "exiting geofence");
                stopRepeatingService(triggeredGeoFenceId);
            }
            Log.e(TAG, "with id = " + triggeredGeoFenceId);
        }
    }

    private void stopRepeatingService(Long triggeredGeoFenceId) {
        handlerThread.quit();//todo stop repeating
    }

    private void startRepeatingService(Long triggeredGeoFenceId) {
        handler.postDelayed(runnableCode, 10000);

    }

    private void startBlockedActivity(String taskName){
        Intent dialogIntent = new Intent(this, BlockedActivity.class);
        dialogIntent.putExtra("taskName", taskName);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }


}
