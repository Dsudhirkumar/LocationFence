package polohalo.ua.locationfence.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.model.App;
import polohalo.ua.locationfence.utils.AppsManager;

/**
 * Created by mac on 3/2/16.
 */
public class BlockedActivity extends AppCompatActivity {

    private static final String TAG ="BlockedActivity" ;
    private App app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null)
            app =AppsManager.getAppByName(savedInstanceState.getString("taskName"), getBaseContext());
        else
            app = AppsManager.getAppByName(getIntent().getExtras().getString("taskName"), getBaseContext());
        setContentView(R.layout.activity_blocked);
        setUpViews();

    }
    private void setUpViews(){
        ImageView appIcon = (ImageView) findViewById(R.id.app_icon);
        TextView appPackage = (TextView) findViewById(R.id.app_package);
        TextView appName = (TextView) findViewById(R.id.app_name);
        appName.setText(app.getLabel());
        appIcon.setImageDrawable(app.getIconDrawable(getBaseContext()));
        appPackage.setText(app.getPackageName());
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString("taskName", app.getLabel());
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }


    @Override
    protected void onPause() {
        super.onPause();
        finish();//to prevent these activities from stacking,
        // only way because service can only start new activities
        // and does not override old ones.
    }
}
