package polohalo.ua.locationfence.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.adapter.LocationListAdapter;
import polohalo.ua.locationfence.model.GeofenceLocation;
import polohalo.ua.locationfence.service.ScreenService;

public class MainActivity extends AppCompatActivity {
    private static final int NEW_LOCATION = 1;
    private MainActivity activity;

    BroadcastReceiver mReceiver=null;

    private static final String TAG = "MainActivity";
    private RecyclerView recList;
    private LinearLayoutManager llm;
    private LocationListAdapter adapter;
    private TextView textGeo;
    private ImageView icGeo;
    private CoordinatorLayout coordinatorLayout;
    private List<GeofenceLocation> locations = new ArrayList<>();
    private LinearLayout bottomView;

    private LinearLayout viewEdit;
    private LinearLayout viewManage;
    private LinearLayout viewDelete;
    private BottomSheetBehavior<LinearLayout> behavior;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        textGeo = (TextView) findViewById(R.id.text_geo_block);
        icGeo = (ImageView) findViewById(R.id.ic_geo_block);
        bottomView =(LinearLayout) coordinatorLayout.findViewById(R.id.bottom_sheet);

        //setUpRecyclerView();
        setUpFAB();
        setUpBottomView();
        setUpListView();
        //launchService();
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setIcon(R.drawable.ic_launcher);

        //IntentFilter filter = new IntentFilter();
        //filter.addAction(Intent.ACTION_SCREEN_ON);
        //filter.addAction(Intent.ACTION_SCREEN_OFF);
        //mReceiver = new ScreenOnReceiver();
        //registerReceiver(mReceiver, filter);

        //Intent startIntent = new Intent(MainActivity.this, ForegroundService.class);
        //startService(startIntent);


    }


    private void setUpBottomView(){
        behavior = BottomSheetBehavior.from(bottomView);
        behavior.setHideable(true);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                // React to state change
                Log.e(TAG, "stateChanged");
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // React to dragging events
                //Log.e(TAG,"onSlide");

            }
        });
        viewDelete = (LinearLayout)bottomView.findViewById(R.id.item_delete);
        viewManage = (LinearLayout)bottomView.findViewById(R.id.item_manage_apps);
        viewEdit = (LinearLayout)bottomView.findViewById(R.id.item_edit);

        viewManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.removeHighlightedItem();//todo doesnt work when returning from app
                setBottomViewState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent intent = new Intent(MainActivity.this, EditActivity.class);
                Log.e(TAG, "highlightedItem = " + adapter.getHighlightedId());
                intent.putExtra("id", adapter.getHighlightedId());
                startActivity(intent);

            }
        });
        viewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.removeHighlightedItem();
                handleListViewUpdate();//todo double update adapter wtf
                setBottomViewState(BottomSheetBehavior.STATE_COLLAPSED);

            }
        });
        viewEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //adapter.removeHighlightedItem();//todo figure out something different to restore view
                setBottomViewState(BottomSheetBehavior.STATE_COLLAPSED);
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                Log.e(TAG, "highlightedItem = " + adapter.getHighlightedId());
                intent.putExtra("id", adapter.getHighlightedId());
                startActivity(intent);

            }
        });

    }
    public void setBottomViewState(int state){
        behavior.setState(state);

    }



    private void setUpFAB(){
        final FloatingActionButton fab = (FloatingActionButton)  findViewById(R.id.add_location_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.e(TAG, "FAB pressed");
                String transitionName = "reveal";
                Intent intent = new Intent(MainActivity.this, LocationActivity.class);
                startActivityForResult(intent, NEW_LOCATION);
                //startActivity(intent);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NEW_LOCATION) {
            if(resultCode == Activity.RESULT_OK){
                handleListViewUpdate();
                }
                else
                    Log.e(TAG, "back");
            }
    }

    private void handleListViewUpdate() {
        locations = GeofenceLocation.getAll();
        if(locations.size()!=0) {
        icGeo.setVisibility(View.GONE);
        textGeo.setVisibility(View.GONE);
        }
        else {
        icGeo.setVisibility(View.VISIBLE);
        textGeo.setVisibility(View.VISIBLE);
        }
        adapter.updateData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.updateData();
    }
    public void launchService() {
        // Construct our Intent specifying the Service
        Intent i = new Intent(this, ScreenService.class);
        // Add extras to the bundle
        i.putExtra("foo", "bar");
        // Start the service
        startService(i);
    }

    private void setUpListView(){
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

/*

    private void setUpRecyclerView(){
        recList = (RecyclerView) findViewById(R.id.appsList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        //adapter = new AppsListAdapter(this);
        recList.setAdapter(adapter);
        //adapter.updateData(AppsManager.getAppsList(getApplicationContext()));
    }*/



    private BroadcastReceiver mainReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int resultCode = intent.getIntExtra("resultCode", RESULT_CANCELED);
            Log.e(TAG, "broadcasting");
            if (resultCode == RESULT_OK) {
                String resultValue = intent.getStringExtra("resultValue");
                Toast.makeText(MainActivity.this, resultValue, Toast.LENGTH_SHORT).show();
            }
        }
    };
}
