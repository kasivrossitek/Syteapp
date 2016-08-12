package com.syte.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.activities.bulletin.BulletinUserReadMoreActivity;
import com.syte.adapters.AdapterBulletinBoardUser;
import com.syte.listeners.OnBulletinBoardUpdate;
import com.syte.models.BulletinBoard;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by khalid.p on 25-02-2016.
 */
public class BulletinBoardUserFragment extends Fragment implements ViewPager.OnPageChangeListener, OnBulletinBoardUpdate {
        private View mRootView;
        private ViewPager mVpBulletinBoard;
        private ImageView mIvLeftIndicator,mIvRightIndicator;
        private LinearLayout mLinLayBulletin;
        private Bundle mBun;
        private String mSyteId;
        private ArrayList<BulletinBoard> bulletinBoards;
        private ArrayList<String> bulletinBoardsIds;
        private Firebase mFireBaseBulletinBoard;
        private EventListenerBulletinBoard eventListenerBulletinBoard;
        private AdapterBulletinBoardUser adapterBulletinBoardUser;
    private YasPasPreferences mYasPasPreferences;
        @Override
        public void onResume()
            {
                super.onResume();
                mVpBulletinBoard=(ViewPager)mRootView.findViewById(R.id.xVpBulletinBoard);
                mVpBulletinBoard.setFadingEdgeLength(0);
                mVpBulletinBoard.setPageTransformer(true, new DepthPageTransformer());
                mVpBulletinBoard.addOnPageChangeListener(this);
                mFireBaseBulletinBoard.addValueEventListener(eventListenerBulletinBoard);
                adapterBulletinBoardUser=new AdapterBulletinBoardUser(getActivity(),bulletinBoards,this, bulletinBoardsIds, mSyteId);
                mVpBulletinBoard.setAdapter(adapterBulletinBoardUser);
                mVpBulletinBoard.invalidate();
                mVpBulletinBoard.setCurrentItem(0);
            }
        @Override
        public void onPause()
            {
                super.onPause();
                mFireBaseBulletinBoard.removeEventListener(eventListenerBulletinBoard);
                adapterBulletinBoardUser=null;
                mVpBulletinBoard.setAdapter(null);
                mVpBulletinBoard=null;
            }
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
            {
                mRootView = inflater.inflate(R.layout.fragment_syte_detail_bulletin_board_user, container, false);
                return mRootView;
            }// END onCreateView()
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
            {
                super.onActivityCreated(savedInstanceState);
                mInItObjects();
                mInItWidgets();


            }// END onActivityCreated()
        private void mInItObjects()
            {
                mBun=getArguments();
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
                //onBulletinListener=(OnBulletinListener)mBun.getSerializable("test");
                mFireBaseBulletinBoard = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(mSyteId);
                bulletinBoards=new ArrayList<>();
                bulletinBoardsIds=new ArrayList<>();
                eventListenerBulletinBoard=new EventListenerBulletinBoard();
                mYasPasPreferences = YasPasPreferences.GET_INSTANCE(getActivity());
            }// END mInItObjects()
        private void mInItWidgets()
            {

                mIvLeftIndicator=(ImageView)mRootView.findViewById(R.id.xIvLeftIndicator);
                mIvRightIndicator=(ImageView)mRootView.findViewById(R.id.xIvRightIndicator);
                mLinLayBulletin=(LinearLayout)mRootView.findViewById(R.id.xLinLayBulletin);
            }//END mInItWidgets()
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
        {}//END onPageScrolled()
        @Override
        public void onPageSelected(int position)
        {
            if(position==0 && bulletinBoards.size()==1)
            {
                mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
                mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
                mIvLeftIndicator.setVisibility(View.GONE);
                mIvRightIndicator.setVisibility(View.GONE);
            }
            else if(position==0 && bulletinBoards.size()>1)
            {
                mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
              //  mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
                mIvRightIndicator.setImageResource(R.drawable.ic_bulletin_more);
                mIvLeftIndicator.setVisibility(View.GONE);
                mIvRightIndicator.setVisibility(View.VISIBLE);
            }
            /*else if(position == bulletinBoards.size()-1 && bulletinBoards.size()>1 && position >0)
            {
                mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_active_14px);
                mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
            }
            else if(bulletinBoards.size()>1 && position>0 && position < bulletinBoards.size()-1)
            {
                mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_active_14px);
                mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
            }*/
        }// END onPageSelected()
        @Override
        public void onPageScrollStateChanged(int state)
        {}// END onPageScrollStateChanged()

    @Override
    public void onBulletinEdit(String paramBulletinId, BulletinBoard paramBulletinBoard) {

    }

    @Override
    public void onBulletinDelete(String paramBulletinId) {

    }

    @Override
    public void onBulletinReadMore(int paramPosition)
        {
            Intent mIntReadMore = new Intent(getActivity(), BulletinUserReadMoreActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString(StaticUtils.IPC_SYTE_ID,mSyteId);
            bundle.putInt(StaticUtils.IPC_BULLETIN_PAGE_POSITION, paramPosition);
            mIntReadMore.putExtras(bundle);
            startActivity(mIntReadMore);
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
                            Iterator<DataSnapshot> iterator2 = dataSnapshot1.child("Likes").getChildren().iterator();
                            while (iterator2.hasNext()) {
                                DataSnapshot dataSnapshot2 = (DataSnapshot) iterator2.next();
                                b.setLiked(dataSnapshot2.getKey().equals(mYasPasPreferences.sGetRegisteredNum()));
                            }
                        } else {
                            b.setLiked(false);
                        }
                        bulletinBoards.add(b);
                        bulletinBoardsIds.add(dataSnapshot1.getKey().toString());
                        if(!it.hasNext())
                        {
                            try
                            {
                                if (!getActivity().isFinishing())
                                {
                                    mLinLayBulletin.setVisibility(View.VISIBLE);
                                    adapterBulletinBoardUser=new AdapterBulletinBoardUser(getActivity(),bulletinBoards,BulletinBoardUserFragment.this,bulletinBoardsIds,mSyteId);
                                    mVpBulletinBoard.setAdapter(adapterBulletinBoardUser);
                                    mVpBulletinBoard.invalidate();

                                    mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
                                    if(bulletinBoards.size()>1)
                                    {
                                      //  mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
                                        mIvRightIndicator.setImageResource(R.drawable.ic_bulletin_more);
                                    }
                                    else
                                    {
                                        mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
                                        mIvRightIndicator.setVisibility(View.GONE);
                                    }
                                }
                            }
                            catch (Exception e){}
                        }
                    }
                    if(bulletinBoards.size()<=0 && !getActivity().isFinishing())
                    {
                        mLinLayBulletin.setVisibility(View.GONE);
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