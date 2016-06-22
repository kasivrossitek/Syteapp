package com.syte.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.syte.R;

/**
 * Created by khalid.p on 09-02-2016.
 */
public class ContactSupportFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
    {
        private View mRootView;
        private SwipeRefreshLayout swipeRefreshLayout;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
        {
            mRootView = inflater.inflate(R.layout.fragment_temp, container, false);
            return mRootView;
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);
            // Initialize fragment
            /*TextView t = (TextView)mRootView.findViewById(R.id.xTvTemp);
            t.setText("Contact Support\nComing Soon");*/
            RecyclerView itemsRecyclerView = (RecyclerView)mRootView.findViewById(R.id.itemsRecyclerView);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            itemsRecyclerView.setLayoutManager(layoutManager);

            swipeRefreshLayout=(SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);
            swipeRefreshLayout.setOnRefreshListener(this);

            swipeRefreshLayout.setColorSchemeColors(0, 0, 0, 0);
            //swipeRefreshLayout.setProgressBackgroundColor(android.R.color.transparent);


            /*SwipeRefreshLayout swipeRefreshLayout=(SwipeRefreshLayout)mRootView.findViewById(R.id.swipeRefreshLayout);

            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    Log.e("here","here");
                    swipeRefreshLayout.setRefreshing(true);


                }
            });
            swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark,
                    R.color.color_orange, R.color.color_dark_orange);*/

        }

        @Override
        public void onRefresh()
            {
                Log.e("Hre","he");
                swipeRefreshLayout.setRefreshing(true);
            }
    }
