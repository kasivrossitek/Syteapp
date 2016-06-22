package com.syte.activities.walkthrough;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.syte.R;
import com.syte.activities.MobileNumberEntryActivity;
import com.viewpagerindicator.CirclePageIndicator;

/**
 * Created by Khalid.p on 29-04-2016.
 */
public class WalkThroughBeforeLoginActivity extends Activity implements ViewPager.OnPageChangeListener, View.OnClickListener {
        private int[] mResources =
                    {
                        R.drawable.walkthrough_1,
                        R.drawable.walkthrough_2,
                        R.drawable.walkthrough_3,
                        R.drawable.walkthrough_4,
                        R.drawable.walkthrough_5
                    };
        private ViewPager mVwPgWalkThrough;
        private CustomPagerAdapter mCustomPagerAdapter;
        private ImageView mIvSkip;
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_walk_through_before_login);
                mInItObjects();
                mInItWidgets();
            }//END onCreate()
        private void mInItObjects()
            {
                mCustomPagerAdapter = new CustomPagerAdapter(this);
            }// END mInItObjects()
        private void mInItWidgets()
            {
                CirclePageIndicator circlePageIndicator =  (CirclePageIndicator)findViewById(R.id.indicator);
                mVwPgWalkThrough = (ViewPager) findViewById(R.id.xVwPgWalkThrough);
                mVwPgWalkThrough.setPageTransformer(true, new DepthPageTransformer());
                mVwPgWalkThrough.addOnPageChangeListener(this);
                mVwPgWalkThrough.setAdapter(mCustomPagerAdapter);
                circlePageIndicator.setViewPager(mVwPgWalkThrough);
                mIvSkip=(ImageView)findViewById(R.id.xIvSkip);
                mIvSkip.setOnClickListener(this);
            } // End mInItWidgets()
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }
        @Override
        public void onPageSelected(int position)
            {
                if(position==4)
                    {
                        mIvSkip.setImageResource(R.drawable.next);
                    }
                else
                    {
                        mIvSkip.setImageResource(R.drawable.skip);
                    }
            }
        @Override
        public void onPageScrollStateChanged(int state)
            {

            }

    @Override
    public void onClick(View v)
        {
            switch (v.getId())
                {
                    case R.id.xIvSkip:
                        {
                            Intent mInt_MNES = new Intent(WalkThroughBeforeLoginActivity.this, MobileNumberEntryActivity.class);
                            Bundle mBun = new Bundle();
                            mBun.putString("keyCC", getIntent().getExtras().getString("keyCC"));
                                mInt_MNES.putExtras(mBun);
                                startActivity(mInt_MNES);
                                finish();
                        }
                    default:break;
                }
        }

    class CustomPagerAdapter extends PagerAdapter
            {
                Context mContext;
                LayoutInflater mLayoutInflater;
                public CustomPagerAdapter(Context context)
                    {
                        mContext = context;
                        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    }
                @Override
                public int getCount()
                    {
                        return mResources.length;
                    }
                @Override
                public boolean isViewFromObject(View view, Object object)
                    {
                        return view == ((LinearLayout) object);
                    }
                @Override
                public Object instantiateItem(ViewGroup container, int position)
                    {
                        View itemView = mLayoutInflater.inflate(R.layout.layout_walk_through_items, container, false);
                        ImageView imageView = (ImageView) itemView.findViewById(R.id.imageView);
                        imageView.setImageResource(mResources[position]);
                        container.addView(itemView);
                        return itemView;
                    }
                @Override
                public void destroyItem(ViewGroup container, int position, Object object)
                    {
                        container.removeView((LinearLayout) object);
                }
            }// END CustomPagerAdapter
        public class DepthPageTransformer implements ViewPager.PageTransformer
        {
            private static final float MIN_SCALE = 0.75f;
            public void transformPage(View view, float position)
            {
                int pageWidth = view.getWidth();
                if (position < -1)
                { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    view.setAlpha(0);
                }
                else if (position <= 0)
                { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    view.setAlpha(1);
                    view.setTranslationX(0);
                    view.setScaleX(1);
                    view.setScaleY(1);
                }
                else if (position <= 1)
                { // (0,1]
                    // Fade the page out.
                    view.setAlpha(1 - position);

                    // Counteract the default slide transition
                    view.setTranslationX(pageWidth * -position);

                    // Scale the page down (between MIN_SCALE and 1)
                    float scaleFactor = MIN_SCALE
                            + (1 - MIN_SCALE) * (1 - Math.abs(position));
                    view.setScaleX(scaleFactor);
                    view.setScaleY(scaleFactor);

                }
                else
                { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    view.setAlpha(0);
                }
            }
        }//END DepthPageTransformer
    }
