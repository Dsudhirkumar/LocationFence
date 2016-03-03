package polohalo.ua.locationfence.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import polohalo.ua.locationfence.R;

/**
 * Created by mac on 3/2/16.
 */
public class BlockedActivity extends AppCompatActivity {

    private static final String TAG ="BlockedActivity" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setUpEnterAnimation();
        setContentView(R.layout.activity_blocked);
        Log.e(TAG, "blocked on " + getIntent().getExtras().getString("taskName"));
    }
}
