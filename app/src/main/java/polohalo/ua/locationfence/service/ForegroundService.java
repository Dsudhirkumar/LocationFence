package polohalo.ua.locationfence.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import polohalo.ua.locationfence.model.GeofenceLocation;
import polohalo.ua.locationfence.utils.AppsManager;

/**
 * Created by mac on 2/20/16.
 */
public class ForegroundService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "ForegroundService";
    Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Do something here on the main thread
            Log.e("Handlers", "Called on  mainThread");
            String foregroundApp = AppsManager.printForegroundTask(ForegroundService.this);
            AppsManager.checkBlacklistForApp(foregroundApp);
            //App.getAllBlacklistedApps()
            handler.postDelayed(runnableCode, 10000);
        }
    };
    private ArrayList<Geofence> mGeofenceList;
    private GoogleApiClient mApiClient;


    //todo try this threading code
    /**HandlerThread thread = new HandlerThread("MyHandlerThread");
thread.start();
Handler handler = new Handler(thread.getLooper());*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate(){
        initGeofence();
        Log.e(TAG, "service onCreate");
    }

    private void initGeofence() {
        mGeofenceList = new ArrayList<>();
        //todo create list of geofence from orm database
        List<GeofenceLocation> locations = GeofenceLocation.getAll();
        Log.e(TAG,"setting up geofence with locations = " + locations.size());
        for(GeofenceLocation location : locations){
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(location.getId().toString())
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setCircularRegion(location.getLatitude(), location.getLongitude(), (float)location.getRadius())
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build();
            mGeofenceList.add(geofence);
        }
        //mGeofenceList.add(new Geofence.Builder().setRequestId("8").setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
        //        .setCircularRegion(50.44858936, 30.45108676, 200).setExpirationDuration(Geofence.NEVER_EXPIRE).build());
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        PendingIntent mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        GeofencingRequest geofenceRequest = new GeofencingRequest.Builder().addGeofences(mGeofenceList).build();
        LocationServices.GeofencingApi.addGeofences(mApiClient, geofenceRequest,
                mGeofenceRequestIntent);


    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
