package polohalo.ua.locationfence.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import polohalo.ua.locationfence.service.GeofenceService;

/**
 * Created by mac on 2/19/16.
 */
public class ScreenOnReceiver extends BroadcastReceiver {
    public static final String TAG = "ScreenOnReceiver";
    private AlarmManager manager;
    private PendingIntent alarmIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.e(TAG, "SCREEN_OFF");
            context.stopService(new Intent(context, GeofenceService.class));
        }

        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.e(TAG, "SCREEN_ON");
            Intent intentService = new Intent(context, GeofenceService.class);
            context.startService(intentService);
        }
        else
            Log.e(TAG, "OTHER");

    }
}
