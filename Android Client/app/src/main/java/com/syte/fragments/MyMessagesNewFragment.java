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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khalid.p on 14-03-2016.
 */
public class MyMessagesNewFragment extends Fragment
    {
        private View mRootView;
        private TabLayout tabLayout;
        private ViewPager viewPager;
        private ViewPagerAdapter mAdapterTab;
        class ViewPagerAdapter extends FragmentPagerAdapter
            {
                public final List<Fragment> mFragmentTab = new ArrayList<>();
                private final List<String> mFragmentTabTitle = new ArrayList<>();
                public ViewPagerAdapter(FragmentManager manager)
                    {
                        super(manager);
                        Bundle bundle = new Bundle();
                        mFragmentTab.add(new MyChatsFragment());
                        mFragmentTab.add(new MySytesChatsFragment());
                        mFragmentTabTitle.add(getString(R.string.my_messages_tab_1_title));
                        mFragmentTabTitle.add(getString(R.string.my_messages_tab_2_title));
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
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
            {
                mRootView = inflater.inflate(R.layout.fragment_my_messages_new, container, false);
                return mRootView;
            }
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
            {
                super.onActivityCreated(savedInstanceState);
                mInItWidgets();
            }// END onActivityCreated()
        private void mInItWidgets()
            {
                viewPager = (ViewPager) mRootView.findViewById(R.id.viewpager);
                tabLayout = (TabLayout) mRootView.findViewById(R.id.tabs);
                mAdapterTab = new ViewPagerAdapter(getChildFragmentManager());
                viewPager.setAdapter(mAdapterTab);
                tabLayout.setupWithViewPager(viewPager);
            }// END mInItWidgets()
    }// END MyMessagesNewFragment()
