package polohalo.ua.locationfence.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.fragment.AppsListFragment;
import polohalo.ua.locationfence.fragment.BlacklistFragment;
import polohalo.ua.locationfence.model.GeofenceLocation;

/**
 * Created by mac on 2/28/16.
 */
public class EditActivity extends AppCompatActivity {
    private static final String TAG = "EditActivity";
    private static final int EDIT_LOCATION = 2;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private GeofenceLocation location;
    private AppsListFragment appsListFragment;
    private BlacklistFragment appsBlacklistFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Intent intent = getIntent();
        Log.e(TAG, "getLong " + intent.getExtras().getLong("id"));
        location = GeofenceLocation.getById(intent.getExtras().getLong("id"));
        setUpViewPager();
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.action_confirm://todo get back
                finish();
                return true;
            case R.id.action_edit_place://todo get back
                Intent intent = new Intent(EditActivity.this, LocationActivity.class);
                intent.putExtra("id", location.getId());//todo parcel doesnt work with this orm library
                startActivityForResult(intent, EDIT_LOCATION);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    private void setUpViewPager() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        appsListFragment = AppsListFragment.newInstance(location.getId());
        appsBlacklistFragment = BlacklistFragment.newInstance(location.getId());

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(appsListFragment, getString(R.string.all_apps));
        adapter.addFragment(appsBlacklistFragment, getString(R.string.blacklist));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        appsListFragment.updateAdapterData();
                        break;
                    case 1:
                        appsBlacklistFragment.updateAdapterData();
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


}
