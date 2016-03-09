package polohalo.ua.locationfence.service;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;

import polohalo.ua.locationfence.model.GeofenceLocation;

/**
 * Created by mac on 2/20/16.
 */
public class ApiClientService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    private static final String TAG = "ApiClientService";

    private ArrayList<Geofence> mGeofenceList;
    private static GoogleApiClient mApiClient;
    private static PendingIntent mGeofenceRequestIntent;
    private static boolean geofenceActive=false;

    public static boolean isGeofenceActive() {
        return geofenceActive;
    }


    //todo try this threading code
    /**HandlerThread thread = new HandlerThread("MyHandlerThread");
thread.start();
Handler handler = new Handler(thread.getLooper());*/

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG,"onBind");

        return null;
    }

    @Override
    public void onCreate(){
        Log.e(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        initGeofence();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();

    }
    public static void stopGeofence( ResultCallback<Status> callback){
        LocationServices.GeofencingApi.removeGeofences(
                mApiClient,
                // This is the same pending intent that was used in addGeofences().
                mGeofenceRequestIntent
        ).setResultCallback(callback); // Result processed in onResult().

    }


    private void initGeofence() {
        mGeofenceList = new ArrayList<>();
        //todo create list of geofence from orm database
        List<GeofenceLocation> locations = GeofenceLocation.getAll();
        Log.e(TAG,"setting up geofence with locations = " + locations.size());
        //lat 50.448533
        //long 30.451056
        for(GeofenceLocation location : locations){
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(location.getId().toString())
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                    .setCircularRegion(50.448533, 30.451056, (float)location.getRadius())
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .build();
            mGeofenceList.add(geofence);
        }
        //mGeofenceList.add(new Geofence.Builder().setRequestId("8").setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
        //        .setCircularRegion(50.44858936, 30.45108676, 200).setExpirationDuration(Geofence.NEVER_EXPIRE).build());
        if(mApiClient==null)
            mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();



    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG,"onConnected");
        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            Log.e(TAG, "noPermission");
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
        //stopSelf();



    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG,"onConnectionSuspended");
        stopSelf();

    }
    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceEventService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG,"onConnectionFailed");
        stopSelf();


    }

}
