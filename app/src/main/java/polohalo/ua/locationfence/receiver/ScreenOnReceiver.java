package polohalo.ua.locationfence.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

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
        //Intent in = new Intent(context, MonitorService.class);
        //context.startService(in);
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.e(TAG, "SCREEN_OFF");
            if(manager!=null)
                manager.cancel(alarmIntent);
        }

        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.e(TAG, "SCREEN_ON");
            if(manager==null) {
                Log.e(TAG, "null manager");
                manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent mIntent = new Intent(context, MonitorReceiver.class);
                alarmIntent = PendingIntent.getBroadcast(context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            }
            manager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), 1000 * 15, alarmIntent);

            //
            //Intent i = new Intent(context, ScreenService.class);
            //i.putExtra("screen_state", screenOff);
            //context.startService(i);
        }
        else
            Log.e(TAG, "OTHER");

    }
}
