package polohalo.ua.locationfence.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import polohalo.ua.locationfence.GeofenceTransitionsIntentService;
import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.geofence.SimpleGeofence;
import polohalo.ua.locationfence.geofence.SimpleGeofenceStore;
import polohalo.ua.locationfence.model.GeofenceLocation;
import polohalo.ua.locationfence.utils.LocationManager;

import static polohalo.ua.locationfence.geofence.Constants.ANDROID_BUILDING_ID;
import static polohalo.ua.locationfence.geofence.Constants.ANDROID_BUILDING_LATITUDE;
import static polohalo.ua.locationfence.geofence.Constants.ANDROID_BUILDING_LONGITUDE;
import static polohalo.ua.locationfence.geofence.Constants.ANDROID_BUILDING_RADIUS_METERS;
import static polohalo.ua.locationfence.geofence.Constants.GEOFENCE_EXPIRATION_TIME;
import static polohalo.ua.locationfence.geofence.Constants.YERBA_BUENA_ID;
import static polohalo.ua.locationfence.geofence.Constants.YERBA_BUENA_LATITUDE;
import static polohalo.ua.locationfence.geofence.Constants.YERBA_BUENA_LONGITUDE;
import static polohalo.ua.locationfence.geofence.Constants.YERBA_BUENA_RADIUS_METERS;

/**
 * Created by mac on 2/23/16.
 */
@RuntimePermissions
public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "LocationActivity";


    private static final LatLng SYDNEY = new LatLng(-33.87365, 151.20689);

    private static final double DEFAULT_RADIUS = 100;

    private GoogleMap mMap;
    private int circleColour;
    public static final double RADIUS_OF_EARTH_METERS = 6371009;
    int progress = 10;
    private GoogleApiClient mApiClient;
    private SimpleGeofenceStore mGeofenceStorage;
    private ArrayList<Geofence> mGeofenceList;
    private SimpleGeofence mAndroidBuildingGeofence;
    private SimpleGeofence mYerbaBuenaGeofence;
    private PendingIntent mGeofenceRequestIntent;
    private DraggableCircle circle;
    private CoordinatorLayout coordinatorLayout;
    private BottomSheetBehavior<View> behavior;
    private TextView radiusText;
    private SeekBar mWidthBar;
    private TextView addressText;
    private TextView regionText;
    private FloatingActionButton fab;
    private ProgressBar addressProgressBar;
    private EditText labelText;
    private String[] address;
    private GeofenceLocation location;
    private Long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setUpEnterAnimation();
        setContentView(R.layout.activity_location);

        //placeholder = (FrameLayout) findViewById(R.id.fragment_placeholder);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        setUpBottomSheet();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        circleColour =ContextCompat.getColor(getBaseContext(),R.color.map_marker_cirle);
        mapFragment.getMapAsync(this);
        setUpGeofence();

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if(extras!=null) {
            id = extras.getLong("id");
            Log.e(TAG, "and the id is " + id);
            //todo we are coming from EditActivity, try to modify location

        }

    }

    private void modifyLocationActivity(Long id) {
        location = GeofenceLocation.getById(id);

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        circle = new DraggableCircle(latLng, location.getRadius());
        addressProgressBar.setProgress((int)location.getRadius());
        if(location.getLabel()!=null)
            if(!location.getLabel().equals(""))
                labelText.setText(location.getLabel());
        addressText.setText(location.getAddressFirst());
        regionText.setText(location.getAddressSecond());

        if(mMap.getCameraPosition().zoom<15.0)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
        else
            mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
        //todo change fab to confirm icon
        fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_confirm, getTheme()));//todo breaks on API<21, fix

    }

    private void setUpGeofence() {
        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mApiClient.connect();
        // Instantiate a new geofence storage area.
        mGeofenceStorage = new SimpleGeofenceStore(this);
        // Instantiate the current List of geofences.
        mGeofenceList = new ArrayList<Geofence>();
        createGeofences();
    }
    private void setUpBottomSheet(){
        final int radiusMin = getResources().getInteger(R.integer.seekBar_start);
        int radiusMax = getResources().getInteger(R.integer.seekBar_end);
        fab = (FloatingActionButton) findViewById(R.id.confirm_location_fab);
        View bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        addressText = (TextView)coordinatorLayout.findViewById(R.id.address);
        labelText = (EditText)coordinatorLayout.findViewById(R.id.label);
        regionText = (TextView)coordinatorLayout.findViewById(R.id.city);
        addressProgressBar = (ProgressBar) coordinatorLayout.findViewById(R.id.progressBar_address_loading);
        behavior = BottomSheetBehavior.from(bottomSheet);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e(TAG, "FAB pressed");
                saveDataAndFinishActivity();
            }
        });
        //behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                if(newState==BottomSheetBehavior.STATE_COLLAPSED){
                    circle.getCenterMarker().remove();
                    circle.getCircle().remove();
                    fab.setVisibility(View.GONE);
                }
                else if(newState==BottomSheetBehavior.STATE_SETTLING)
                    fab.setVisibility(View.VISIBLE);
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
            }
        });
        radiusText = (TextView) findViewById(R.id.seekBar_radius);
        mWidthBar = (SeekBar) findViewById(R.id.seekBar);
        mWidthBar.setMax(radiusMax);
        mWidthBar.setProgress(50);
        mWidthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                radiusText.setText(getResources().getString(R.string.radius) + " "
                        + (seekBar.getProgress() + radiusMin) + " m");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                circle.getCircle().setRadius(seekBar.getProgress() + radiusMin);

            }
        });


    }

    private void saveDataAndFinishActivity() {
        GeofenceLocation object = new GeofenceLocation();
        object.setLatitude(circle.getCenterMarker().getPosition().latitude);
        object.setLongitude(circle.getCenterMarker().getPosition().longitude);
        object.setRadius(circle.getCircle().getRadius());
        object.setLabel(labelText.getText().toString());
        object.setAddress(address);
        // store to database
        if(id!=null)
            GeofenceLocation.updateItem(object, id);
        GeofenceLocation.addItem(object);
        Log.e(TAG, "getting back " + GeofenceLocation.getAll().size());

        Intent intent = getIntent();
        //intent.putExtra("location", Parcels.wrap(object));
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void createGeofences() {
        // Create internal "flattened" objects containing the geofence data.
        mAndroidBuildingGeofence = new SimpleGeofence(
                ANDROID_BUILDING_ID,                // geofenceId.
                ANDROID_BUILDING_LATITUDE,
                ANDROID_BUILDING_LONGITUDE,
                ANDROID_BUILDING_RADIUS_METERS,
                GEOFENCE_EXPIRATION_TIME,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT
        );
        mYerbaBuenaGeofence = new SimpleGeofence(
                YERBA_BUENA_ID,                // geofenceId.
                YERBA_BUENA_LATITUDE,
                YERBA_BUENA_LONGITUDE,
                YERBA_BUENA_RADIUS_METERS,
                GEOFENCE_EXPIRATION_TIME,
                Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT
        );

        // Store these flat versions in SharedPreferences and add them to the geofence list.
        mGeofenceStorage.setGeofence(ANDROID_BUILDING_ID, mAndroidBuildingGeofence);
        mGeofenceStorage.setGeofence(YERBA_BUENA_ID, mYerbaBuenaGeofence);
        mGeofenceList.add(mAndroidBuildingGeofence.toGeofence());
        mGeofenceList.add(mYerbaBuenaGeofence.toGeofence());
    }


    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void setMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            Log.e(TAG, "app has location permission");
        } else {
            // Show rationale and request permission.
            Log.e(TAG, "app doesn't have location permission");
            //todo add functionality to ask only once etc, button on location
        }

    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    public void onLocationDenied() {
        Log.e(TAG, "app doesn't have location permission");
    }

    @OnNeverAskAgain(Manifest.permission.ACCESS_FINE_LOCATION)
    public void onLocationNeverAsk() {
        Log.e(TAG, "app WON'T have location permission");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        LocationActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LocationActivityPermissionsDispatcher.setMyLocationWithCheck(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);//to remove unnecessary buttons

        //todo move marker to user location
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));x
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                if(circle!=null){//todo clear up
                    circle.getCenterMarker().remove();
                    circle.getCircle().remove();
                }
                circle = new DraggableCircle(latLng, DEFAULT_RADIUS);

                if(mMap.getCameraPosition().zoom<15.0)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                else
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

                //todo store these circles, they should coresspond to locations
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                addressProgressBar.setVisibility(View.VISIBLE);
                LocationManager.getInstance().getMyLocationAddress(LocationActivity.this, latLng, new LocationManager.OnAddressListener() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, "success " + result);
                        address = result.split("\\n");
                        if(address.length>1) {
                            regionText.setText(address[1]);
                            regionText.setVisibility(View.VISIBLE);
                        }
                        addressText.setText(address[0]);
                        addressText.setVisibility(View.VISIBLE);
                        addressProgressBar.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError() {
                        Log.e(TAG, "error");
                        regionText.setVisibility(View.GONE);
                        addressText.setVisibility(View.GONE);
                        addressProgressBar.setVisibility(View.GONE);

                    }
                });

            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Log.e(TAG, "onMarkerClick");
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                circle.getCenterMarker().remove();
                circle.getCircle().remove();
                return false;
            }
        });
        if(id!=null)//code if coming from EditActivity
            modifyLocationActivity(id);

    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.e(TAG, "onConnected");
        LocationActivityPermissionsDispatcher.populateGeofencesWithCheck(this);
        //finish();
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void populateGeofences() {//todo i dont know when service is started, so wtf
        mGeofenceRequestIntent = getGeofenceTransitionPendingIntent();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }//todo fucking dispatcher doesnt work
        GeofencingRequest geofenceRequest = new GeofencingRequest.Builder().addGeofences(mGeofenceList).build();
        LocationServices.GeofencingApi.addGeofences(mApiClient, geofenceRequest,
                mGeofenceRequestIntent);
        Toast.makeText(this, "starting service", Toast.LENGTH_SHORT).show();

    }
    private PendingIntent getGeofenceTransitionPendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();
        Log.e(TAG, "Connection to Google Play services failed with error code " + errorCode);

    }


    public class DraggableCircle {

        private final Marker centerMarker;

        private final Circle circle;

        private double radius;

        public DraggableCircle(LatLng center, double radius) {
            this.radius = radius;
            centerMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(progress)
                    .strokeColor(circleColour)
                    .fillColor(circleColour));
        }

        public DraggableCircle(LatLng center, LatLng radiusLatLng) {
            this.radius = toRadiusMeters(center, radiusLatLng);
            centerMarker = mMap.addMarker(new MarkerOptions()
                    .position(center)
                    .draggable(true));
            circle = mMap.addCircle(new CircleOptions()
                    .center(center)
                    .radius(radius)
                    .strokeWidth(progress)
                    .strokeColor(circleColour)
                    .fillColor(circleColour));
        }
        public Marker getCenterMarker(){
            return this.centerMarker;
        }
        public Circle getCircle(){
            return this.circle;
        }

    }
    private static LatLng toRadiusLatLng(LatLng center, double radius) {
        double radiusAngle = Math.toDegrees(radius / RADIUS_OF_EARTH_METERS) /
                Math.cos(Math.toRadians(center.latitude));
        return new LatLng(center.latitude, center.longitude + radiusAngle);
    }

    private static double toRadiusMeters(LatLng center, LatLng radius) {
        float[] result = new float[1];
        Location.distanceBetween(center.latitude, center.longitude,
                radius.latitude, radius.longitude, result);
        return result[0];
    }
//todo i've seen location.distnaceTo code, try out
}
