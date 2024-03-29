package polohalo.ua.locationfence.activity;

import android.app.AppOpsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;

import java.util.ArrayList;
import java.util.List;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.adapter.LocationListAdapter;
import polohalo.ua.locationfence.fragment.PermissionDialogFragment;
import polohalo.ua.locationfence.model.GeofenceLocation;
import polohalo.ua.locationfence.service.NotificationService;
import polohalo.ua.locationfence.utils.AppsManager;

public class MainActivity extends AppCompatActivity {
    private static final int NEW_LOCATION = 1;
    private static final int REQUEST_WRITE_SETTINGS = 4;
    private static final int EDIT_LOCATION = 2;
    private static final int EDIT_APPS = 3;
    private MainActivity activity;

    BroadcastReceiver mReceiver = null;

    private static final String TAG = "MainActivity";
    private RecyclerView recList;
    private LinearLayoutManager llm;
    private LocationListAdapter adapter;
    private TextView textGeo;
    private TextView toggleText;
    private ImageView icGeo;
    private SwitchCompat toggleButton;
    private CoordinatorLayout coordinatorLayout;
    private List<GeofenceLocation> locations = new ArrayList<>();
    private View bottomView;

    private LinearLayout viewEdit;
    private LinearLayout viewManage;
    private LinearLayout viewDelete;
    private BottomSheetBehavior behavior;
    private GoogleApiClient mApiClient;
    private ArrayList<Geofence> mGeofenceList;
    private Intent foregroundIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        textGeo = (TextView) findViewById(R.id.text_geo_block);
        toggleText = (TextView) findViewById(R.id.toggle_text);
        toggleButton = (SwitchCompat) findViewById(R.id.toggle_button);

        icGeo = (ImageView) findViewById(R.id.ic_geo_block);
        bottomView =  findViewById(R.id.design_bottom_sheet);

        setUpFAB();
        setUpBottomView();
        setUpListView();
        setUpToolbar();
        setUpNotificationService();
        //launchService();
        //todo need to acquire a permission for api+23
        //checkForUsagePermission();


        //todo navigate user to settings app


    }

    private void setUpNotificationService(){
        if(AppsManager.isMyServiceRunning(this, NotificationService.class)) {
            toggleButton.setChecked(true);
        }
        else {
            toggleButton.setChecked(true);
            startNotificationService();
        }
    }


    private void setUpToolbar() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    startNotificationService();
                } else {
                    stopService(foregroundIntent);
                }
            }
        });
    }

    private void checkForUsagePermission() {
        if(Build.VERSION.SDK_INT>=23)
            if(!hasUsagePermission(this)) {
                //Log.e(TAG, "no permission");
                showPermissionDialog();
            }
    }
    private boolean hasUsagePermission(Context context){
        AppOpsManager appOps = (AppOpsManager)
                context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        return granted;

    }

    private void showPermissionDialog(){
        FragmentManager fm = getSupportFragmentManager();
        PermissionDialogFragment dialog = PermissionDialogFragment.newInstance();
        dialog.show(fm, "");
    }



    private void setUpBottomView() {
        behavior = BottomSheetBehavior.from(bottomView);
        behavior.setHideable(true);

        viewDelete = (LinearLayout) bottomView.findViewById(R.id.item_delete);
        viewManage = (LinearLayout) bottomView.findViewById(R.id.item_manage_apps);
        viewEdit = (LinearLayout) bottomView.findViewById(R.id.item_edit);

        viewManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBottomViewState(BottomSheetBehavior.STATE_HIDDEN);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                intent.putExtra("id", adapter.getHighlightedId());
                adapter.uncheckLastChecked();
                startActivityForResult(intent, EDIT_APPS);


            }
        });
        viewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.removeHighlightedItem();
                handleListViewUpdate();//todo double update adapter wtf
                setBottomViewState(BottomSheetBehavior.STATE_HIDDEN);

            }
        });
        viewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //adapter.removeHighlightedItem();//todo figure out something different to restore view
                setBottomViewState(BottomSheetBehavior.STATE_HIDDEN);
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                //Log.e(TAG, "highlightedItem = " + adapter.getHighlightedId());
                intent.putExtra("id", adapter.getHighlightedId());
                adapter.uncheckLastChecked();
                startActivityForResult(intent, EDIT_LOCATION);
            }
        });

    }

    public void setBottomViewState(int state) {
        behavior.setState(state);
    }

    private void setUpFAB() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_location_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivityForResult(intent, NEW_LOCATION);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //todo if we played with location, we should restart the service
        if (requestCode == REQUEST_WRITE_SETTINGS) {
            if(!hasUsagePermission(this))
                finish();//no chance to continue using app without permission, cant just check resultCode, may be null
            //Log.e(TAG, "onActivityResult, request for settings, result codes " + requestCode + " " + resultCode);
        }
        else if (requestCode == NEW_LOCATION || requestCode==EDIT_LOCATION) {
            handleListViewUpdate();
            handleNotificationServiceState();//data has changed, update
        }
    }

    private void handleListViewUpdate() {
        locations = GeofenceLocation.getAll();
        if (locations.size() != 0) {
            //todo hide toggle in toolbar
            icGeo.setVisibility(View.GONE);
            textGeo.setVisibility(View.GONE);
            toggleText.setVisibility(View.VISIBLE);
            toggleButton.setChecked(true);
            toggleButton.setVisibility(View.VISIBLE);
        } else {
            icGeo.setVisibility(View.VISIBLE);
            toggleText.setVisibility(View.GONE);
            toggleButton.setChecked(false);
            toggleButton.setVisibility(View.GONE);
            textGeo.setVisibility(View.VISIBLE);
        }
        adapter.updateData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForUsagePermission();
        adapter.updateData();
        handleNotificationServiceState();
    }

    private void handleNotificationServiceState() {
        if(toggleButton.isChecked()){
            if(GeofenceLocation.getAll().size()!=0 )
                startNotificationService();
        }
        else
            stopService(foregroundIntent);
    }
    /** handles conditions at which service is useless to start
     * */
    private void startNotificationService(){
        if(GeofenceLocation.getAll().size()!=0) {
            foregroundIntent = new Intent(MainActivity.this, NotificationService.class);
            startService(foregroundIntent);
        }
    }

    private void setUpListView() {
        recList = (RecyclerView) findViewById(R.id.locationsList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        adapter = new LocationListAdapter(this);//bottomView is needed to show dialog to edit/delete item
        recList.setAdapter(adapter);
        handleListViewUpdate();
        setUpBottomView();
    }


}
