package com.syte.activities.bulletin;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.activities.HomeActivity;
import com.syte.adapters.AdapterBulletinBoardReadMoreUser;
import com.syte.models.AnalyticsVisitor;
import com.syte.models.BulletinBoard;
import com.syte.utils.SendAnalyticsVisitor;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by khalid.p on 03-03-2016.
 */
public class BulletinUserReadMoreActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, View.OnClickListener {
    private RelativeLayout mRelLayBack;
    private ViewPager mVpBulletinBoard;
    private ImageView mIvLeftIndicator,mIvRightIndicator;
    private RelativeLayout mRellayPageIndicator;
    private Bundle mBun;
    private String mSyteId;
    private ArrayList<BulletinBoard> bulletinBoards;
    private ArrayList<String> bulletinBoardsIds;
    private Firebase mFireBaseBulletinBoard;
    private EventListenerBulletinBoard eventListenerBulletinBoard;
    private AdapterBulletinBoardReadMoreUser adapterBulletinBoardReadMoreUser;
    private ProgressDialog mPrgDia;
    private int mPagePosition;
    private YasPasPreferences mYasPasPref;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapterBulletinBoardReadMoreUser=null;
        mVpBulletinBoard.setAdapter(null);
        mVpBulletinBoard=null;
    }
    @Override
    public void onResume()
        {
            super.onResume();
            mPrgDia.show();
            mFireBaseBulletinBoard.addValueEventListener(eventListenerBulletinBoard);
        }
    @Override
    public void onPause()
        {
            super.onPause();
            mFireBaseBulletinBoard.removeEventListener(eventListenerBulletinBoard);
        }
    @Override
    protected void onCreate(Bundle savedInstanceState)

        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_bulletin_user_read_more);
            mInItObjects();
            mInItWidgets();
            adapterBulletinBoardReadMoreUser=new AdapterBulletinBoardReadMoreUser(BulletinUserReadMoreActivity.this,bulletinBoards, bulletinBoardsIds, mSyteId);
            mVpBulletinBoard.setAdapter(adapterBulletinBoardReadMoreUser);
            mVpBulletinBoard.invalidate();
            mVpBulletinBoard.setCurrentItem(0);
        }// END onActivityCreated()
    private void mInItObjects()
        {
            mBun=getIntent().getExtras();
            mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
            mPagePosition=mBun.getInt(StaticUtils.IPC_BULLETIN_PAGE_POSITION);
            mFireBaseBulletinBoard = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(mSyteId);
            bulletinBoards=new ArrayList<>();
            bulletinBoardsIds=new ArrayList<>();
            eventListenerBulletinBoard=new EventListenerBulletinBoard();
            mPrgDia = new ProgressDialog(BulletinUserReadMoreActivity.this);
            mPrgDia.setMessage(getString(R.string.prg_bar_wait));
            mPrgDia.setCancelable(false);
            mYasPasPref=YasPasPreferences.GET_INSTANCE(BulletinUserReadMoreActivity.this);
        }// END mInItObjects()
    private void mInItWidgets()
        {
            mVpBulletinBoard=(ViewPager)findViewById(R.id.xVpBulletinBoard1);
            mVpBulletinBoard.setFadingEdgeLength(0);
            mVpBulletinBoard.setPageTransformer(true, new DepthPageTransformer());
            mVpBulletinBoard.addOnPageChangeListener(this);
            mIvLeftIndicator=(ImageView)findViewById(R.id.xIvLeftIndicator);
            mIvRightIndicator=(ImageView)findViewById(R.id.xIvRightIndicator);
            mRellayPageIndicator=(RelativeLayout)findViewById(R.id.xRellayPageIndicator);
            mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
            mRelLayBack.setOnClickListener(this);
        }//END mInItWidgets()
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {}//END onPageScrolled()
    @Override
    public void onPageSelected(int position)
        {
            mPagePosition=position;
            mSetPage(mPagePosition);
            mSendAnalyticsData(bulletinBoardsIds.get(position));

        }// END onPageSelected()
    private void mSendAnalyticsData(String paramBulletinId)
        {
            AnalyticsVisitor analyticsSyteCumBulletin = new AnalyticsVisitor();
            analyticsSyteCumBulletin.setVisitorRegisteredNum(mYasPasPref.sGetRegisteredNum());
            analyticsSyteCumBulletin.setVisitorGender(mYasPasPref.sGetUserGender());
            analyticsSyteCumBulletin.setVisitedSyteId(mSyteId);
            analyticsSyteCumBulletin.setVisitedBulletinId(paramBulletinId);
            analyticsSyteCumBulletin.setVisitorLatitude(HomeActivity.LOC_ACTUAL.getLatitude());
            analyticsSyteCumBulletin.setVisitorLongitude(HomeActivity.LOC_ACTUAL.getLongitude());
            analyticsSyteCumBulletin.setVisitedTime(ServerValue.TIMESTAMP);
            new SendAnalyticsVisitor(analyticsSyteCumBulletin);
        }// END mSendAnalyticsData()
    private void mSetPage(int paramPos)
        {
            if(paramPos==0 && bulletinBoards.size()==1)
            {
                mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
                mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
            }
            else if(paramPos==0 && bulletinBoards.size()>1)
            {
                mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
               // mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
                mIvRightIndicator.setImageResource(R.drawable.ic_bulletin_more);
                mIvLeftIndicator.setVisibility(View.GONE);
                mIvRightIndicator.setVisibility(View.VISIBLE);
            }
           /* else if(paramPos == bulletinBoards.size()-1 && bulletinBoards.size()>1 && paramPos >0)
            {
                mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_active_14px);
                mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
            }
            else if(bulletinBoards.size()>1 && paramPos>0 && paramPos < bulletinBoards.size()-1)
            {
                mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_active_14px);
                mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
            }*/
        } // END mSetPage()
    @Override
    public void onPageScrollStateChanged(int state)
        {}// END onPageScrollStateChanged()

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.xRelLayBack:
            {
                finish();
                break;
            }
            default:break;
        }
    }


    private class EventListenerBulletinBoard implements ValueEventListener
    {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot)
        {

            bulletinBoards.removeAll(bulletinBoards);
            bulletinBoardsIds.removeAll(bulletinBoardsIds);
            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
            while (it.hasNext())
            {
                DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                BulletinBoard b = dataSnapshot1.getValue(BulletinBoard.class);
                if (dataSnapshot1.hasChild("Likes")) {
                    Log.d("prefsponsor", "" + mYasPasPref.sGetRegisteredNum());
                    if (dataSnapshot1.child("Likes").hasChild(mYasPasPref.sGetRegisteredNum())) {
                        b.setLiked(true);
                    } else {
                        b.setLiked(false);

                    }
                } else {
                    b.setLiked(false);
                }
                Log.d("bulletinboardkey", "" + dataSnapshot1.getKey().toString());
                bulletinBoards.add(b);
                bulletinBoardsIds.add(dataSnapshot1.getKey().toString());
                if(!it.hasNext())
                {
                    try
                    {
                        if (!isFinishing())
                        {
                            mRellayPageIndicator.setVisibility(View.VISIBLE);
                            adapterBulletinBoardReadMoreUser=new AdapterBulletinBoardReadMoreUser(BulletinUserReadMoreActivity.this,bulletinBoards,bulletinBoardsIds,mSyteId);
                            mVpBulletinBoard.setAdapter(adapterBulletinBoardReadMoreUser);
                            mVpBulletinBoard.invalidate();

                            mVpBulletinBoard.setCurrentItem(mPagePosition);
                            mSetPage(mPagePosition);
                            mPagePosition=0;


                        }
                    }
                    catch (Exception e){}
                    mPrgDia.dismiss();

                }
            }






            if(bulletinBoards.size()<=0 && !isFinishing())
            {
                mRellayPageIndicator.setVisibility(View.GONE);
                finish();
            }
        }
        @Override
        public void onCancelled(FirebaseError firebaseError)
        {

        }
    }// END EventListenerBulletinBoard()
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
}// END BulletinBoardUserFragment
