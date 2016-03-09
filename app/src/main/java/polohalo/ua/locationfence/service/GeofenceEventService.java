package polohalo.ua.locationfence.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import polohalo.ua.locationfence.activity.BlockedActivity;
import polohalo.ua.locationfence.receiver.ScreenOnReceiver;
import polohalo.ua.locationfence.utils.AppsManager;

public class GeofenceEventService extends Service {//changed IntentService to Service

    private static final String TAG = "GeofenceEventService";
    static HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
    static Handler handler;
    private static int period;
    static private long triggeredGeoFenceId;
    private static boolean running = false;
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.e(TAG, "Handler called on  customThread, service state = " + running);
            if (running) {
                if (AppsManager.blacklistContains(triggeredGeoFenceId)) {
                    if (AppsManager.foregroundTaskIsBlocked(AppsManager.printForegroundTask(GeofenceEventService.this))) {
                        startBlockedActivity(AppsManager.printForegroundTask(GeofenceEventService.this));
                        Log.e(TAG, "blocked this app");
                    }
                }
                handler.postDelayed(runnableCode, 8000);//todo play with delay and check if all the created activities are the same or new ones
            }
        }
    };
    private ScreenOnReceiver mScreenStateReceiver;

    public GeofenceEventService() {
        super();
    }

    public static void setRunning(boolean running) {
        GeofenceEventService.running = running;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;
        Log.e(TAG, "onCreate, here");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand, receiver noticed geofence event");
        period  = AppsManager.getRepeatingPeriod(getBaseContext());
        if(mScreenStateReceiver==null) {//todo why we need another receiver?
           // IntentFilter screenStateFilter = new IntentFilter();
           // screenStateFilter.addAction(Intent.ACTION_SCREEN_ON);
           // screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
           // registerReceiver(mScreenStateReceiver, screenStateFilter);
        }

        Log.e(TAG, "onHandleIntent1 " + String.valueOf(intent == null));
        GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        if(geoFenceEvent!=null)
        if (geoFenceEvent.hasError()||geoFenceEvent.getTriggeringGeofences()==null) {//todo first of all requires permission, second - still null
            //int errorCode = geoFenceEvent.getErrorCode();
            Log.e(TAG, "Location Services error");
            stopRepeatingService(triggeredGeoFenceId);
        } else {
            Log.e(TAG, "hip hop " + geoFenceEvent.getTriggeringGeofences().get(0).getRequestId());
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
        return START_STICKY;//todo try out START_REDELIVER_INTENT from documentation
    }

    public void quitThread(){
        Log.e(TAG, "quitThread");
        handler.removeCallbacks(runnableCode);
        handlerThread.quit();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        running = false;
        unregisterReceiver(mScreenStateReceiver);
        mScreenStateReceiver=null;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void stopRepeatingService(Long triggeredGeoFenceId) {
        handlerThread.quit();//todo stop repeating
    }

    private void startRepeatingService(Long triggeredGeoFenceId) {
        if(handlerThread.isAlive())
            handlerThread.quit();
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
        handler.postDelayed(runnableCode, 1000);

    }

    private void startBlockedActivity(String taskName){
        Intent dialogIntent = new Intent(this, BlockedActivity.class);
        dialogIntent.putExtra("taskName", taskName);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);
    }


}
