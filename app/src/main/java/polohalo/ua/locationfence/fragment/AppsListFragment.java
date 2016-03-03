package polohalo.ua.locationfence.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.adapter.AppsListAdapter;
import polohalo.ua.locationfence.model.App;

/**
 * Created by mac on 2/28/16.
 */
public class AppsListFragment extends Fragment {

    private static final String TAG = "AppsListFragment";
    private RecyclerView recList;
    private LinearLayoutManager llm;
    private AppsListAdapter adapter;
    private Long location;
    private ArrayList<App> apps;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_appslist, container, false);
        setUpRecyclerView(view);
        return view;

    }

    private void setUpRecyclerView(View view){
        recList = (RecyclerView) view.findViewById(R.id.appsList);
        recList.setHasFixedSize(true);
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        Log.e(TAG, "setUpRecycler with " + location);
        adapter = new AppsListAdapter(getActivity(), location);
        recList.setAdapter(adapter);
        updateAdapterData();
    }


    public static AppsListFragment newInstance(Long location) {
        AppsListFragment fragment = new AppsListFragment();
        fragment.location  = location;
        return fragment;//todo maybe add more data
    }

    public void updateAdapterData() {
        adapter.updateData();
    }
}
