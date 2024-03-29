package polohalo.ua.locationfence.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import polohalo.ua.locationfence.model.App;

/**
 * Created by mac on 2/20/16.
 */
public class AppsManager {
    private static final String TAG = "AppsManager";
    private static List<App> blockedApps;


    public static ArrayList<App> getAppsList(Context context){
        final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<App> apps = new ArrayList<>();

        for (ApplicationInfo packageInfo : packages) {
            apps.add(new App(packageInfo.packageName, (String) pm.getApplicationLabel(packageInfo)));
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }

        return filterApps(apps);
    }

    private static ArrayList<App> filterApps(ArrayList<App> apps) {
        ArrayList<App> filteredApps = new ArrayList<>();
        for(App app : apps ){
            if(app.getPackageName().contains("com.android.") || app.getPackageName().contains("polohalo.ua.locationfence"))//todo maybe other filters are needed
                continue;
            else
                filteredApps.add(app);
        }
        return filteredApps;
    }

    public static String printForegroundTask(Service service) {//todo check if permission is granted
        String currentApp = "NULL";
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            UsageStatsManager usm = (UsageStatsManager) service.getSystemService(Context.USAGE_STATS_SERVICE);
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
            ActivityManager am = (ActivityManager)service.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> tasks = am.getRunningAppProcesses();
            currentApp = tasks.get(0).processName;
        }
        //Log.e(TAG, "Current App in foreground is: " + currentApp);
        return currentApp;
    }

    public static void checkBlacklistForApp(String foregroundApp) {
        List<App> appsList = App.getAllBlacklistedApps();
        for(App app : appsList){
            
        }

    }

    public static boolean blacklistContains(Long triggeredGeoFenceId) {
        blockedApps = App.getBlacklistedApps(triggeredGeoFenceId);
        //Log.e(TAG, blockedApps.size() + "   " + App.getAllBlacklistedApps().size() + "  " + triggeredGeoFenceId);
        for(App app : blockedApps){
            //Log.e(TAG,"blacklist contains " + app.getLocationId() +  "  " + triggeredGeoFenceId);
            if(app.getLocationId().equals(triggeredGeoFenceId))
                return true;
        }
        return false;
    }

    public static boolean foregroundTaskIsBlocked(String taskName) {
        for(App app :blockedApps){
           // Log.e(TAG, app.getPackageName() +  "  " + taskName);
            if(app.getPackageName().equals(taskName))
                return true;
        }
        return false;
    }

    public static void setRepeatingPeriod(Context context, int period) {
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor edit = pref.edit();
        edit.putInt("period", period);
        edit.commit();
    }
    public static int getRepeatingPeriod(Context context){
        SharedPreferences pref =
                PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt("period", 10);

    }



    public static App getAppByPackage(String taskName, Context context) {
            List<App> apps = getAppsList(context);
            for(App app : apps){
                if(taskName.equals(app.getPackageName()))
                    return app;
            }
            return new App();
        }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
