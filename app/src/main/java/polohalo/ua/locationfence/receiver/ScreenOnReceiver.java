package polohalo.ua.locationfence.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import polohalo.ua.locationfence.service.ApiClientService;
import polohalo.ua.locationfence.service.GeofenceEventService;

/**
 * Created by mac on 2/19/16.
 */
public class ScreenOnReceiver extends BroadcastReceiver {
    public static final String TAG = "ScreenOnReceiver";


    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e(TAG, "onReceive");
        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            //Log.e(TAG, "SCREEN_OFF");
            //GeofenceEventService.setRunning(false);
            //todo unregister geofence

            ApiClientService.stopGeofence(new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    //Log.e(TAG, "stoping geofence"+status.getStatus());
                }
            });
            GeofenceEventService.setRunning(false);
            context.stopService(new Intent(context, ApiClientService.class));

        }

        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            //Log.e(TAG, "SCREEN_ON");
            Intent intentService = new Intent(context, ApiClientService.class);
            context.startService(intentService);
            GeofenceEventService.setRunning(true);

            //register geofence
        }
        else
            Log.e(TAG, "OTHER");

    }
}
