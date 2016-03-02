package polohalo.ua.locationfence.service;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by mac on 2/18/16.
 */
public class MonitorService extends IntentService {
    public static final String TAG = "packageName";
    public MonitorService() {
        // Used to name the worker thread, important only for debugging.
        super("test-service");
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent");
        String val = intent.getStringExtra("foo");
        Intent in = new Intent(TAG);
        in.putExtra("resultCode", Activity.RESULT_OK);
        in.putExtra("revultValue", val);
        LocalBroadcastManager.getInstance(this).sendBroadcast(in);
    }
}
