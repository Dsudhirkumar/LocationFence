package polohalo.ua.locationfence.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import polohalo.ua.locationfence.service.NotificationService;

/**
 * Created by mac on 3/4/16.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e(TAG, "onReceiver");
        //todo trying to use notification service
        Intent intentService = new Intent(context, NotificationService.class);
        context.startService(intentService);
    }
}
