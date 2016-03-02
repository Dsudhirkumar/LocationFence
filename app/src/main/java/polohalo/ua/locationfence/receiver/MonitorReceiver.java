package polohalo.ua.locationfence.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by mac on 2/19/16.
 */
public class MonitorReceiver extends BroadcastReceiver {
    public static final String TAG = "MonitorReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "onReceive");
    }
}
