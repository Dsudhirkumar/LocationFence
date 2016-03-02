package polohalo.ua.locationfence.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import polohalo.ua.locationfence.activity.MainActivity;

/**
 * Created by mac on 2/20/16.
 */
public class ForegroundService extends Service {
    private static final String TAG = "ForegroundService";
    Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.e("Handlers", "Called on main thread");
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction("test");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle("Truiton Music Player")
                .setTicker("Truiton Music Player")
                .setContentText("My Music")
                .setContentIntent(pendingIntent)
                .setOngoing(true).build();
        startForeground(123,
                notification);
// Run the above code block on the main thread after 2 seconds
        Log.e(TAG, "before");
        handler.postDelayed(runnableCode, 5000);//todo if we post from another thread, where all work is done, it would be nice
        Log.e(TAG, "after");

        //stopForeground(true);
        //stopSelf();

        return START_STICKY;

    }
}
