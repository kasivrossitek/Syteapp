package com.syte.activities.analytics;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.syte.R;
import com.syte.fragments.SyteAnalyticsDaysFragment;
import com.syte.fragments.SyteAnalyticsMonthsFragment;
import com.syte.utils.StaticUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kumar.m on 21-03-2016.
 */
public class SyteAnalyticsActivity extends AppCompatActivity implements View.OnClickListener
{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter mAdapterTab;
    private Bundle mBundle;
    private String mSyteId;

    @Override
    protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_syte_analytics);

            findViewById(R.id.xRelLayBack).setOnClickListener(this);
            viewPager = (ViewPager) findViewById(R.id.viewpager);
            tabLayout = (TabLayout) findViewById(R.id.tabs);



            mBundle = new Bundle();
            mSyteId = getIntent().getExtras().getString(StaticUtils.IPC_SYTE_ID);
            mBundle.putString(StaticUtils.IPC_SYTE_ID,mSyteId);

            mAdapterTab = new ViewPagerAdapter(getSupportFragmentManager());
            viewPager.setAdapter(mAdapterTab);
            tabLayout.setupWithViewPager(viewPager);
        }// END onCreate()

    @Override
    public void onClick(View v)
        {
            switch (v.getId())
                {
                    case R.id.xRelLayBack:
                        {
                            finish();
                            break;
                        }

                    default:break;
                }
        }// END onClick()

    class ViewPagerAdapter extends FragmentPagerAdapter
        {
            public final List<Fragment> mFragmentTab = new ArrayList<>();
            private final List<String> mFragmentTabTitle = new ArrayList<>();

            public ViewPagerAdapter(FragmentManager manager)
                {
                    super(manager);

                    SyteAnalyticsDaysFragment syteAnalyticsDaysFragment = new SyteAnalyticsDaysFragment();
                    syteAnalyticsDaysFragment.setArguments(mBundle);

                    SyteAnalyticsMonthsFragment syteAnalyticsMonthsFragment = new SyteAnalyticsMonthsFragment();
                    syteAnalyticsMonthsFragment.setArguments(mBundle);

                    mFragmentTab.add(syteAnalyticsDaysFragment);
                    mFragmentTab.add(syteAnalyticsMonthsFragment);
                    mFragmentTabTitle.add(getString(R.string.syte_analytics_page_days_title));
                    mFragmentTabTitle.add(getString(R.string.syte_analytics_page_months_title));
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
