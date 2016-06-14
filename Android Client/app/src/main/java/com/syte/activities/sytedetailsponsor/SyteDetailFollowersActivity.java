package com.syte.activities.sytedetailsponsor;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.carrier.CarrierService;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.cast.Cast;
import com.syte.R;
import com.syte.adapters.AdapterFollowers;
import com.syte.models.Followers;
import com.syte.models.Syte;
import com.syte.utils.StaticUtils;
import com.syte.widgets.TvRobortoMedium;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by khalid.p on 02-03-2016.
 */
public class SyteDetailFollowersActivity extends Activity implements View.OnClickListener {
    private RelativeLayout mRelLayBack, mRelTeam;
    private LinearLayout linLayContacts, linLaywhatsApp;
    private RecyclerView mRvTeamMembers;
    private ArrayList<Followers> mFollowerses;
    private AdapterFollowers mAdapterFollowers;
    private LinearLayoutManager layoutManager;
    private Context mContext;
    private Bundle mBun;
    private String mSyteId;
    private Syte mSyte;

    private Firebase mFirebaseFollowers;
    private EventListenerFollowers eventListenerFollowers;
    private ProgressDialog mPrgDia;
    private TextView mTvCenterLbl;
    private ImageView close_view;
    //invite friends
    private View invitefndsview;
    private TvRobortoMedium btnInvitefriends;

    @Override
    protected void onResume() {
        super.onResume();
        mPrgDia.show();
        mFirebaseFollowers.addValueEventListener(eventListenerFollowers);
    } // END onResume()

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseFollowers.removeEventListener(eventListenerFollowers);

    }// END onPause()

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapterFollowers = null;
        mRvTeamMembers.setAdapter(null);
        mRvTeamMembers = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_syte_details_followers);
        mInItObjects();
        mInItWidgets();
    }// END onCreate()

    private void mInItObjects() {
        layoutManager = new LinearLayoutManager(SyteDetailFollowersActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mContext = this;
        mBun = getIntent().getExtras();
        mFollowerses = new ArrayList<>();
        mSyteId = mBun.getString(StaticUtils.IPC_SYTE_ID);
        mSyte=getIntent().getExtras().getParcelable(StaticUtils.IPC_SYTE);
        mFirebaseFollowers = new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.FOLLOWERS_YASPAS);
        eventListenerFollowers = new EventListenerFollowers();
        mPrgDia = new ProgressDialog(SyteDetailFollowersActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
    }// END mInItObjects

    private void mInItWidgets() {
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mRvTeamMembers = (RecyclerView) findViewById(R.id.xRvTeamMembers);
        mRvTeamMembers.setLayoutManager(layoutManager);
        mTvCenterLbl = (TextView) findViewById(R.id.xTvCenterLbl);
        //Invite friends by kasi 28/5/16
        mRelTeam = (RelativeLayout) findViewById(R.id.xRelTeam);
        close_view = (ImageView) findViewById(R.id.xIvDelete);
        invitefndsview = (View) findViewById(R.id.xInvitefriendsID);
        btnInvitefriends = (TvRobortoMedium) findViewById(R.id.xTvInvitefnds);
        linLayContacts = (LinearLayout) findViewById(R.id.xLinlayWhatsApp);
        linLaywhatsApp = (LinearLayout) findViewById(R.id.xLinlayContacts);
        linLaywhatsApp.setOnClickListener(this);
        linLayContacts.setOnClickListener(this);
        btnInvitefriends.setOnClickListener(this);
        close_view.setOnClickListener(this);
    }// END mInItWidgets()

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }

            case R.id.xTvInvitefnds:
                btnInvitefriends.setVisibility(View.GONE);
                mRelTeam.animate().translationYBy(-100);
                invitefndsview.setVisibility(View.VISIBLE);
                break;
            case R.id.xIvDelete://delete the invite friends view by kasi
                if (btnInvitefriends.getVisibility() == View.GONE) {
                    mRelTeam.animate().translationYBy(100);
                    btnInvitefriends.setVisibility(View.VISIBLE);
                    invitefndsview.setVisibility(View.GONE);

                }
                break;
            case R.id.xLinlayContacts: //for phone contacts by kasi
                Intent getcontact = new Intent(getApplicationContext(), PhoneContactsActivity.class);
                Bundle mBdl = new Bundle();
                mBdl.putParcelableArrayList(StaticUtils.IPC_FOLLOWERS, mFollowerses);
                mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                mBdl.putParcelable(StaticUtils.IPC_SYTE,mSyte);
                getcontact.putExtras(mBdl);
                startActivity(getcontact);

                break;
            case R.id.xLinlayWhatsApp://for whatsapp contacts by kasi
                Intent whatsappcontact = new Intent(getApplicationContext(), WhatsappActivity.class);
                Bundle mBd = new Bundle();
                mBd.putParcelableArrayList(StaticUtils.IPC_FOLLOWERS, mFollowerses);
                mBd.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                mBd.putParcelable(StaticUtils.IPC_SYTE,mSyte);
                whatsappcontact.putExtras(mBd);
                startActivity(whatsappcontact);
                break;
            default:
                break;

        }
    }// END onClick()

    private class EventListenerFollowers implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mFollowerses.removeAll(mFollowerses);
            if (dataSnapshot.getValue() != null) {
                Iterator<DataSnapshot> iteratorFollowers = dataSnapshot.getChildren().iterator();
                while (iteratorFollowers.hasNext()) {
                    DataSnapshot dataSnapshotFollower = (DataSnapshot) iteratorFollowers.next();
                    mFollowerses.add(dataSnapshotFollower.getValue(Followers.class));
                }
            }
            mAdapterFollowers = null;
            mAdapterFollowers = new AdapterFollowers(mContext, mFollowerses, ((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)));
            if (mFollowerses.size() > 0) {
                mTvCenterLbl.setVisibility(View.GONE);
            } else {
                mTvCenterLbl.setVisibility(View.VISIBLE);
            }
            mRvTeamMembers.setAdapter(mAdapterFollowers);
            try {
                mPrgDia.dismiss();
            } catch (Exception e) {
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {
            try {
                mPrgDia.dismiss();
            } catch (Exception e) {
            }
        }
    }// END EventListenerFollowers
}// END SyteDetailFollowersActivity
