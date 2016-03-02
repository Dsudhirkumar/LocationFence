package polohalo.ua.locationfence.service;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import polohalo.ua.locationfence.activity.MainActivity;

/**
 * Created by mac on 2/19/16.
 */
public class ScreenService extends IntentService {
    private static final String TAG = "ScreenService";
    BroadcastReceiver mReceiver=null;

    public ScreenService() {
        super("test-service");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent started service");
        // Register receiver that handles screen on and screen off logic
        //IntentFilter filter = new IntentFilter();
        //filter.addAction(Intent.ACTION_SCREEN_ON);
        //filter.addAction(Intent.ACTION_SCREEN_OFF);
        //mReceiver = new MonitorReceiver();
        //registerReceiver(mReceiver, filter);
        printForegroundTask();
        startForbiddenActivity();
        stopSelf();
    }

    private void startForbiddenActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void printForegroundTask() {
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
            long time = System.currentTimeMillis();
            List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,  time - 1000*1000, time);
            if (appList != null && appList.size() > 0) {
                SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                for (UsageStats usageStats : appList) {
                    mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                }
                if (mySortedMap != null && !mySortedMap.isEmpty()) {
                    currentApp = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                }
            }
        } else {
            ActivityManager am = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }

        Log.e(TAG, "Current App in foreground is: " + currentApp);
    }


    @Override
    public void onCreate() {
        super.onCreate();

         //Toast.makeText(getBaseContext(), "Service on create", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "onCreate");


    }
    @Override
    public void onDestroy() {

        Log.i(TAG, "Service destroyed");
        if(mReceiver!=null)
            unregisterReceiver(mReceiver);

    }
}
