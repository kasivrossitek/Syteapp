package com.syte.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.activities.bulletin.AddBulletinBoardActivity;
import com.syte.activities.bulletin.BulletinSponsorReadMoreActivity;
import com.syte.activities.bulletin.EditBulletinBoard;
import com.syte.adapters.AdapterBulletinBoardSponsor;
import com.syte.listeners.OnBulletinBoardUpdate;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.BulletinBoard;
import com.syte.models.Syte;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by khalid.p on 26-02-2016.
 */
public class BulletinBoardSponsorFragment extends Fragment implements View.OnClickListener, ViewPager.OnPageChangeListener, OnBulletinBoardUpdate, OnCustomDialogsListener {
    private View mRootView;
    private TextView mTvAddBulletin;
    private ViewPager mVpBulletinBoard;
    private ImageView mIvLeftIndicator, mIvRightIndicator;
    private Bundle mBun;
    private Syte mSyte;
    private String mSyteId;
    private ArrayList<BulletinBoard> bulletinBoards;
    private ArrayList<String> bulletinBoardsIds;
    private Firebase mFireBaseBulletinBoard;
    private EventListenerBulletinBoard eventListenerBulletinBoard;
    private AdapterBulletinBoardSponsor adapterBulletinBoardSponsor;
    private YasPasPreferences mYasPasPreferences;
    private NetworkStatus mNetworkStatus;
    private ProgressDialog mPrgDia;
    private String globalDeleteBulletinId = "";

    @Override
    public void onResume() {
        super.onResume();
        mVpBulletinBoard = (ViewPager) mRootView.findViewById(R.id.xVpBulletinBoard);
        mVpBulletinBoard.setFadingEdgeLength(0);
        mVpBulletinBoard.setPageTransformer(true, new DepthPageTransformer());
        mVpBulletinBoard.addOnPageChangeListener(this);

        adapterBulletinBoardSponsor = new AdapterBulletinBoardSponsor(getActivity(), bulletinBoards, bulletinBoardsIds, this,mSyteId);
        mVpBulletinBoard.setAdapter(adapterBulletinBoardSponsor);
        mVpBulletinBoard.invalidate();
        mVpBulletinBoard.setCurrentItem(0);
        mFireBaseBulletinBoard.addValueEventListener(eventListenerBulletinBoard);
        //UpdatebulletinOwner();
    }

    @Override
    public void onPause() {
        super.onPause();
        mFireBaseBulletinBoard.removeEventListener(eventListenerBulletinBoard);
        adapterBulletinBoardSponsor = null;
        mVpBulletinBoard.setAdapter(null);
        mVpBulletinBoard = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_syte_detail_bulletin_board_sponsor, container, false);
        return mRootView;
    }// End onCreateView()

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInItObjects();
        mInItWidgets();

    }// END onActivityCreated()

    private void mInItObjects() {
        mBun = getArguments();
        mSyte = mBun.getParcelable(StaticUtils.IPC_SYTE);
        mSyteId = mBun.getString(StaticUtils.IPC_SYTE_ID);
        mFireBaseBulletinBoard = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(mSyteId);
        bulletinBoards = new ArrayList<>();
        bulletinBoardsIds = new ArrayList<>();
        eventListenerBulletinBoard = new EventListenerBulletinBoard();
        mNetworkStatus = new NetworkStatus(getActivity());
        mPrgDia = new ProgressDialog(getActivity());
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
        mYasPasPreferences = YasPasPreferences.GET_INSTANCE(getActivity());

    }// END mInItObjects()

    private void mInItWidgets() {
        mTvAddBulletin = (TextView) mRootView.findViewById(R.id.xTvAddBulletin);
        mTvAddBulletin.setOnClickListener(this);
        mIvLeftIndicator = (ImageView) mRootView.findViewById(R.id.xIvLeftIndicator);
        mIvRightIndicator = (ImageView) mRootView.findViewById(R.id.xIvRightIndicator);
    }//END mInItWidgets()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xTvAddBulletin: {
                Intent mIntAddBulletinBoard = new Intent(getActivity(), AddBulletinBoardActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                mIntAddBulletinBoard.putExtras(bundle);
                getActivity().startActivity(mIntAddBulletinBoard);
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
        if (position == 0 && bulletinBoards.size() == 1) {
            mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
            mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
            mIvLeftIndicator.setVisibility(View.GONE);
            mIvRightIndicator.setVisibility(View.GONE);
        } else if (position == 0 && bulletinBoards.size() > 1) {
            mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
            // mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
            mIvRightIndicator.setImageResource(R.drawable.ic_bulletin_more);
            mIvLeftIndicator.setVisibility(View.GONE);
            mIvRightIndicator.setVisibility(View.VISIBLE);
        }/* else if (position == bulletinBoards.size() - 1 && bulletinBoards.size() > 1 && position > 0) {
            mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_active_14px);
            mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
        } else if (bulletinBoards.size() > 1 && position > 0 && position < bulletinBoards.size() - 1) {
            mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_active_14px);
            mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
        }*/
    }// END onPageSelected()

    @Override
    public void onPageScrollStateChanged(int state) {
    }// END onPageScrollStateChanged()

    @Override
    public void onBulletinEdit(String paramBulletinId, BulletinBoard paramBulletinBoard) {
        if (mNetworkStatus.isNetworkAvailable()) {
            Intent mInt_EditBulletinBoard = new Intent(getActivity(), EditBulletinBoard.class);
            Bundle bundle = new Bundle();
            bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
            bundle.putString(StaticUtils.IPC_BULLETIN_ID, paramBulletinId);
            bundle.putParcelable(StaticUtils.IPC_BULLETIN, paramBulletinBoard);
            mInt_EditBulletinBoard.putExtras(bundle);
            getActivity().startActivity(mInt_EditBulletinBoard);
        } else {
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(), this);
            customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
        }
    }

    @Override
    public void onBulletinDelete(final String paramBulletinId) {
        if (mNetworkStatus.isNetworkAvailable()) {
            globalDeleteBulletinId = paramBulletinId;
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(), this);
            customDialogs.sShowDialog_Common(YasPasMessages.DELETE_BULLETIN_SUBJECT, YasPasMessages.DELETE_BULLETIN_HEADING, null, "CANCEL", "DELETE", "deleteBulletin", false, false);
        } else {
            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(), this);
            customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
        }
    }

    @Override
    public void onBulletinReadMore(int paramPosition) {

        Intent mIntReadMore = new Intent(getActivity(), BulletinSponsorReadMoreActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
        bundle.putInt(StaticUtils.IPC_BULLETIN_PAGE_POSITION, paramPosition);
        mIntReadMore.putExtras(bundle);
        startActivity(mIntReadMore);

    }

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish) {
        if (paramDialogType == CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(startMain);
            getActivity().finish();
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
                if (!it.hasNext()) {
                    try {
                        if (!getActivity().isFinishing()) {
                            mVpBulletinBoard.setVisibility(View.VISIBLE);
                            adapterBulletinBoardSponsor = new AdapterBulletinBoardSponsor(getActivity(), bulletinBoards, bulletinBoardsIds, BulletinBoardSponsorFragment.this, mSyteId);
                            mVpBulletinBoard.setAdapter(adapterBulletinBoardSponsor);
                            mVpBulletinBoard.invalidate();
                            mIvLeftIndicator.setVisibility(View.GONE);

                            mIvLeftIndicator.setImageResource(R.drawable.ic_yp_left_arrow_in_active_14px);
                            if (bulletinBoards.size() > 1) {
                                //  mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_active_14px);
                                mIvRightIndicator.setVisibility(View.VISIBLE);
                                mIvRightIndicator.setImageResource(R.drawable.ic_bulletin_more);
                            } else {
                                mIvRightIndicator.setVisibility(View.GONE);
                                mIvRightIndicator.setImageResource(R.drawable.ic_yp_right_arrow_in_active_14px);
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
            if (bulletinBoards.size() <= 0 && !getActivity().isFinishing()) {
                mVpBulletinBoard.setVisibility(View.GONE);
                mIvLeftIndicator.setVisibility(View.GONE);
                mIvRightIndicator.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    }// END EventListenerBulletinBoard()

    private void UpdatebulletinOwner() {//kasi
        Firebase mFBBulletinowner = new Firebase(StaticUtils.YASPAS_BULLETIN_BOARD_URL).child(mSyteId);
        mFBBulletinowner.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Iterator<DataSnapshot> it = dataSnapshot.getChildren().iterator();
                    while (it.hasNext()) {
                        DataSnapshot dataSnapshot1 = (DataSnapshot) it.next();
                        Log.d("datasnapshot", dataSnapshot1.getKey() + "syte" + mSyte.getMobileNo());
                        if (!dataSnapshot1.hasChild("owner")) {
                            Log.d("no owner", "syte" + mSyte.getOwner());
                            dataSnapshot1.getRef().setValue("owner", mSyte.getMobileNo());
                        }
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


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

    private void mUpdateYasPasLatestBulletin(String paramSub) {
        // Updating added bulletin's subject/body as "latestBulletin" for particular YasPas
        Firebase oFrBse = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child("latestBulletin");
        oFrBse.setValue(paramSub, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                mPrgDia.dismiss();

            }
        });
    } // END mUpdateYasPasLatestBulletin()

    private void mDeleteBulletin(final String paramBulletinId) {
        mPrgDia.show();
        globalDeleteBulletinId = "";
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
                        mUpdateYasPasLatestBulletin(b.getSubject());

                    } catch (Exception e) {
                        mUpdateYasPasLatestBulletin("");
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}// END BulletinBoardUserFragment