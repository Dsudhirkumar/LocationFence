package polohalo.ua.locationfence.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import polohalo.ua.locationfence.model.App;

/**
 * Created by mac on 2/20/16.
 */
public class AppsManager {
    private static final String TAG = "AppsManager";


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
        return apps;
    }
}
