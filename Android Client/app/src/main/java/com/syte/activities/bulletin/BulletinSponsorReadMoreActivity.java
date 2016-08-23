package com.syte.activities.bulletin;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.adapters.AdapterBulletinBoardReadMoreSponsor;
import com.syte.listeners.OnBulletinBoardUpdate;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.BulletinBoard;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by khalid.p on 03-03-2016.
 */
public class BulletinSponsorReadMoreActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, OnBulletinBoardUpdate, OnCustomDialogsListener {
    private RelativeLayout mRelLayBack;
    private ViewPager mVpBulletinBoard;
    private ImageView mIvLeftIndicator, mIvRightIndicator;
    private Bundle mBun;
    private String mSyteId;
    private ArrayList<BulletinBoard> bulletinBoards;
    private ArrayList<String> bulletinBoardsIds;
    private Firebase mFireBaseBulletinBoard;
    private EventListenerBulletinBoard eventListenerBulletinBoard;
    private AdapterBulletinBoardReadMoreSponsor adapterBulletinBoardReadMoreSponsor;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private int mPagePosition;
    private String globalDeleteBulletinId = "";
    private YasPasPreferences mYasPasPreferences;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapterBulletinBoardReadMoreSponsor = null;
        mVpBulletinBoard.setAdapter(null);
        mVpBulletinBoard = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPrgDia.show();
        mFireBaseBulletinBoard.addValueEventListener(eventListenerBulletinBoard);
    }

    @Override
    public void onPause() {
        super.onPause();
        mFireBaseBulletinBoard.removeEventListener(eventListenerBulletinBoard);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)

    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bulletin_sponsor_read_more);
        mInItObjects();
        mInItWidgets();

        adapterBulletinBoardReadMoreSponsor = new AdapterBulletinBoardReadMoreSponsor(BulletinSponsorReadMoreActivity.this, bulletinBoards, bulletinBoardsIds, this, mSyteId);
        mVpBulletinBoard.setAdapter(adapterBulletinBoardReadMoreSponsor);
        mVpBulletinBoard.invalidate();
        mVpBulletinBoard.setCurrentItem(0);
    }// END onActivityCreated()

    private void mInItObjects() {
        mBun = getIntent().getExtras();
        mSyteId = mBun.getString(StaticUtils.IPC_SYTE_ID);
        mPagePosition = mBun.getInt(StaticUtils.IPC_BULLETIN_PAGE_POSITION);
        mFireBaseBulletinBoard = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(mSyteId);
        bulletinBoards = new ArrayList<>();
        bulletinBoardsIds = new ArrayList<>();
        eventListenerBulletinBoard = new EventListenerBulletinBoard();
        mNetworkStatus = new NetworkStatus(BulletinSponsorReadMoreActivity.this);
        mPrgDia = new ProgressDialog(BulletinSponsorReadMoreActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setIndeterminate(true);
        mPrgDia.setCancelable(false);
        mYasPasPreferences = YasPasPreferences.GET_INSTANCE(this);
    }// END mInItObjects()

    private void mInItWidgets() {
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mVpBulletinBoard = (ViewPager) findViewById(R.id.xVpBulletinBoard2);
        // mVpBulletinBoard.setAdapter(new AdapterBulletinBoardSponsor(BulletinSponsorReadMoreActivity.this, bulletinBoards, bulletinBoardsIds,this));
        mVpBulletinBoard.setFadingEdgeLength(0);
        mVpBulletinBoard.setPageTransformer(true, new DepthPageTransformer());
        mVpBulletinBoard.addOnPageChangeListener(this);
        mIvLeftIndicator = (ImageView) findViewById(R.id.xIvLeftIndicator);
        mIvRightIndicator = (ImageView) findViewById(R.id.xIvRightIndicator);
    }//END mInItWidgets()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            default:
                break;
        }
    }// END onClick()

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }//END onPageScrolled()

    @Override
    public void onPageSelected(int position) {
        //mPagePosition=position;
        mSetPage(position);
    }// END onPageSelected()

    @Override
    public void onPageScrollStateChanged(int state) {
    }// END onPageScrollStateChanged()

    private void mSetPage(int paramPos) {
        if (paramPos == 0 && bulletinBoards.size() == 1) {
            mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
            mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
        } else if (paramPos == 0 && bulletinBoards.size() > 1) {
            mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
            // mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
            mIvRightIndicator.setImageResource(R.drawable.ic_bulletin_more);
            mIvLeftIndicator.setVisibility(View.GONE);
            mIvRightIndicator.setVisibility(View.VISIBLE);
        } /*else if (paramPos == bulletinBoards.size() - 1 && bulletinBoards.size() > 1 && paramPos > 0) {
            mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_active_14px);
            mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
        } else if (bulletinBoards.size() > 1 && paramPos > 0 && paramPos < bulletinBoards.size() - 1) {
            mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_active_14px);
            mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
        }*/
    } // END mSetPage()

    @Override
    public void onBulletinEdit(String paramBulletinId, BulletinBoard paramBulletinBoard) {
        if (mNetworkStatus.isNetworkAvailable()) {
            Intent mInt_EditBulletinBoard = new Intent(BulletinSponsorReadMoreActivity.this, EditBulletinBoard.class);
            Bundle bundle = new Bundle();
            bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
            bundle.putString(StaticUtils.IPC_BULLETIN_ID, paramBulletinId);
            bundle.putParcelable(StaticUtils.IPC_BULLETIN, paramBulletinBoard);
            mInt_EditBulletinBoard.putExtras(bundle);
            startActivity(mInt_EditBulletinBoard);
        } else {
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(BulletinSponsorReadMoreActivity.this, this);
            customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
        }
    }

    @Override
    public void onBulletinDelete(final String paramBulletinId) {
        if (mNetworkStatus.isNetworkAvailable()) {
            globalDeleteBulletinId = paramBulletinId;
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(BulletinSponsorReadMoreActivity.this, this);
            customDialogs.sShowDialog_Common(YasPasMessages.DELETE_BULLETIN_SUBJECT, YasPasMessages.DELETE_BULLETIN_HEADING, null, "NO", "YES", "deleteBulletin", false, false);

           /* *//*final ProgressDialog mPrgDia1=new ProgressDialog(BulletinSponsorReadMoreActivity.this);
            mPrgDia1.setMessage(getString(R.string.prg_bar_wait));
            mPrgDia1.setCancelable(false);
            mPrgDia1.show();*//*
            mPrgDia.show();
            Firebase mFireBaseSpecificBulletinBoard = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(mSyteId).child(paramBulletinId);
            mFireBaseSpecificBulletinBoard.removeValue();
            mFireBaseBulletinBoard.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.hasChild(paramBulletinId))
                    {
                        Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                        try
                        {
                            DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                            BulletinBoard b = dataSnapshot1.getValue(BulletinBoard.class);
                            mUpdateYasPasLatestBulletin(b.getSubject(),mPrgDia);

                        }
                        catch (Exception e)
                        {
                            mUpdateYasPasLatestBulletin("",mPrgDia);
                        }
                    }
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });*/

        } else {
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(BulletinSponsorReadMoreActivity.this, this);
            customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
        }
    }

    @Override
    public void onBulletinReadMore(int paramPosition) {

    }


    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
            finish();
        } else if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("deleteBulletin")) {
            globalDeleteBulletinId = "";
        }

    }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw")) {
            startActivity(new Intent(Settings.ACTION_SETTINGS));
        } else if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("deleteBulletin")) {
            mDeleteBulletin(globalDeleteBulletinId);
        }
    }

    private class EventListenerBulletinBoard implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            bulletinBoards.removeAll(bulletinBoards);
            bulletinBoardsIds.removeAll(bulletinBoardsIds);
            Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
            while (it.hasNext()) {
                DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                BulletinBoard b = dataSnapshot1.getValue(BulletinBoard.class);
                if (dataSnapshot1.hasChild("Likes")) {
                    Log.d("prefsponsor", "" + mYasPasPreferences.sGetRegisteredNum());
                    if (dataSnapshot1.child("Likes").hasChild(mYasPasPreferences.sGetRegisteredNum())) {
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
                if (!it.hasNext()) {
                    try {
                        if (!isFinishing()) {
                            //mVpBulletinBoard.setVisibility(View.VISIBLE);
                            adapterBulletinBoardReadMoreSponsor = new AdapterBulletinBoardReadMoreSponsor(BulletinSponsorReadMoreActivity.this, bulletinBoards, bulletinBoardsIds, BulletinSponsorReadMoreActivity.this, mSyteId);
                            mVpBulletinBoard.setAdapter(adapterBulletinBoardReadMoreSponsor);
                            mVpBulletinBoard.invalidate();

                            mVpBulletinBoard.setCurrentItem(mPagePosition);
                            mSetPage(mPagePosition);
                            mPagePosition = 0;

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (mPrgDia.isShowing()) {
                mPrgDia.dismiss();
            }

        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }// END EventListenerBulletinBoard()

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();
            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1);
                view.setTranslationX(0);
                view.setScaleX(1);
                view.setScaleY(1);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0);
            }
        }
    }//END DepthPageTransformer

    private void mUpdateYasPasLatestBulletin(String paramSub, final ProgressDialog dialog) {
        // Updating added bulletin's subject/body as "latestBulletin" for particular YasPas
        Firebase oFrBse = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child("latestBulletin");
        oFrBse.setValue(paramSub, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {


                dialog.dismiss();
                mPagePosition = 0;
                if (bulletinBoards.size() <= 0 && !isFinishing()) {
                    finish();
                }

            }
        });
    } // END mUpdateYasPasLatestBulletin()

    private void mDeleteBulletin(final String paramBulletinId) {
        mPrgDia.show();
        globalDeleteBulletinId = "";
        mAddRewardPoints(StaticUtils.REWARD_POINTS_DELETE_BULLETIN);
        Firebase mFireBaseSpecificBulletinBoard = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(mSyteId).child(paramBulletinId);
        mFireBaseSpecificBulletinBoard.removeValue();
        mFireBaseBulletinBoard.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.hasChild(paramBulletinId)) {
                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                    try {
                        DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                        BulletinBoard b = dataSnapshot1.getValue(BulletinBoard.class);
                        mUpdateYasPasLatestBulletin(b.getSubject(), mPrgDia);

                    } catch (Exception e) {
                        mUpdateYasPasLatestBulletin("", mPrgDia);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void mAddRewardPoints(final int rewardPoints) {

        Firebase mFireBsYasPasObj = new Firebase(StaticUtils.YASPASEE_URL).child(mYasPasPreferences.sGetRegisteredNum());
        mFireBsYasPasObj.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null && dataSnapshot != null) {
                    if (dataSnapshot.hasChild("rewards")) {
                        Long rewards = (Long) dataSnapshot.child("rewards").getValue();
                        int totalrewards = rewards.intValue();
                        totalrewards = totalrewards + rewardPoints;
                        StaticUtils.addReward(mYasPasPreferences.sGetRegisteredNum(), totalrewards);
                    } else {
                        StaticUtils.addReward(mYasPasPreferences.sGetRegisteredNum(), rewardPoints);
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }//END REWARDS
}// END BulletinBoardUserFragment