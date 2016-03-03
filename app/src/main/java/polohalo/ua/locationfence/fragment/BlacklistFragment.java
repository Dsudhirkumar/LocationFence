package polohalo.ua.locationfence.fragment;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import polohalo.ua.locationfence.R;
import polohalo.ua.locationfence.adapter.AppsBlacklistAdapter;
import polohalo.ua.locationfence.model.App;

/**
 * Created by mac on 2/28/16.
 */
public class BlacklistFragment extends Fragment {

    private RecyclerView recList;
    private LinearLayoutManager llm;
    private AppsBlacklistAdapter adapter;
    private List<App> blacklistedApps;
    private Long location;
    private CoordinatorLayout mainLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_blacklist, container, false);
        mainLayout = (CoordinatorLayout)view;
        setUpRecyclerView(view);
        return view;

    }

    public static BlacklistFragment newInstance(Long location) {
        BlacklistFragment fragment = new BlacklistFragment();
        fragment.location  = location;
        return fragment;//todo maybe add more data
    }

    private void setUpRecyclerView(View view){
        recList = (RecyclerView) view.findViewById(R.id.appsList);
        recList.setHasFixedSize(true);
        recList.setItemAnimator(new DefaultItemAnimator());
        llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);
        adapter = new AppsBlacklistAdapter(getActivity(), location, mainLayout);
        recList.setAdapter(adapter);
        adapter.updateData();//todo filter by location
    }

    public void updateAdapterData() {
        adapter.updateData();
    }
}
