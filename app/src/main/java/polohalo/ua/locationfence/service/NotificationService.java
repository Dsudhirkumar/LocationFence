package polohalo.ua.locationfence.service;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.activity.MainActivity;
import polohalo.ua.locationfence.receiver.ScreenOnReceiver;

/**
 * Created by mac on 3/4/16.
 */
public class NotificationService extends Service {


    private static final String TAG = "NotificationService";
    private static final String MAIN_ACTION = "main";
    private static final int FOREGROUND_SERVICE = 4;

    private ScreenOnReceiver mReceiver;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand ");
            Log.e(TAG, "Received Start Foreground Intent ");
            Intent notificationIntent = new Intent(this, MainActivity.class);
            notificationIntent.setAction(MAIN_ACTION);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    notificationIntent, 0);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                    .setContentTitle("Geofence service")
                    .setContentText("Touch to turn the service off")
                    .setSmallIcon(R.drawable.ic_pin_drop)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true);
            startForeground(FOREGROUND_SERVICE,
                    notification.build());
            registerReceiver();//todo we must start ApiClientService NOW, not on screen change
            startApiClient();
        //} else if (intent.getAction().equals(Constants.STOPFOREGROUND_ACTION)) {
        //    Log.e(TAG, "Received Stop Foreground Intent");
        //    stopForeground(true);
        //    stopSelf();
        return START_STICKY;

    }

    private void startApiClient() {
        Log.e(TAG, "initial ApiCLient start");
        Intent intentService = new Intent(NotificationService.this, ApiClientService.class);
        //GeofenceEventService.setRunning(true);
        startService(intentService);
        GeofenceEventService.setRunning(true);

        //register geofence
    }

    private void registerReceiver(){//todo it only registers screen of/off shouls be also ApiClientService
        Log.e(TAG, "registeringReceiver");
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new ScreenOnReceiver();
        registerReceiver(mReceiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        //todo maybe stop GeofenceService
        Log.i(TAG, "In onDestroy");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
