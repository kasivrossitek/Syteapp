package com.syte.fragments;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.syte.R;
import com.syte.activities.HomeActivity;
import com.syte.utils.YasPasPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khalid.p on 09-02-2016.
 */
public class MyNotificationsFragment extends Fragment
    {
        private View mRootView;
        private TabLayout tabLayout;
        private ViewPager viewPager;
        private ViewPagerAdapter mAdapterTab;
        private YasPasPreferences mPref;
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
        {
            mRootView = inflater.inflate(R.layout.fragment_my_notifications, container, false);
            return mRootView;
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
        {
            super.onActivityCreated(savedInstanceState);
            HomeActivity homeActivity = (HomeActivity)getActivity();
            mPref = YasPasPreferences.GET_INSTANCE(getActivity());
            mPref.sSetNotificationIconUpdate(false);
            homeActivity.sUpdateNotificationIcon();
            homeActivity=null;
            viewPager = (ViewPager) mRootView.findViewById(R.id.viewpager);
            tabLayout = (TabLayout) mRootView.findViewById(R.id.tabs);

            mAdapterTab = new ViewPagerAdapter(getChildFragmentManager());
            viewPager.setAdapter(mAdapterTab);
            tabLayout.setupWithViewPager(viewPager);
        }

        class ViewPagerAdapter extends FragmentPagerAdapter
        {
            public final List<Fragment> mFragmentTab = new ArrayList<>();
            private final List<String> mFragmentTabTitle = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager)
            {
                super(manager);

                mFragmentTab.add(new NotificationsYouFragment());
                mFragmentTab.add(new NotificationsMySytesFragment());
                mFragmentTabTitle.add(getString(R.string.notification_page_tab_you_title));
                mFragmentTabTitle.add(getString(R.string.notification_page_tab_mysytes_title));
            }

            @Override
            public Fragment getItem(int position)
            {
                return mFragmentTab.get(position);
            }

            @Override
            public int getCount()
            {
                return mFragmentTab.size();
            }

            @Override
            public CharSequence getPageTitle(int position)
            {
                return mFragmentTabTitle.get(position);
            }
        }// END ViewPagerAdapter
    }
