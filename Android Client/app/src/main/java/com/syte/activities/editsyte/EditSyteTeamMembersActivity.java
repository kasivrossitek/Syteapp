package com.syte.activities.editsyte;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.activities.addsyte.AddSyteAddTeamMemberActivity;
import com.syte.activities.addsyte.AddSyteEditTeamMemberActivity;
import com.syte.adapters.AdapterTeamMembers;
import com.syte.listeners.OnEditButtonListener;
import com.syte.models.YasPasTeam;
import com.syte.models.YasPasTeams;
import com.syte.utils.StaticUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by khalid.p on 02-03-2016.
 */
public class EditSyteTeamMembersActivity extends Activity implements View.OnClickListener, OnEditButtonListener {
        private RelativeLayout mRelLayBack;
        private TextView mTvAddTeamMember;
        private RecyclerView mRvTeamMembers;
        private ArrayList<YasPasTeams> mTeamMembers;
        private AdapterTeamMembers mAdapetrTeamMember;
        private LinearLayoutManager layoutManager;
        private Context mContext;
        private Bundle mBun;
        private String mSyteId;
        private Firebase mFirebaseTeamMembers;
        private EventListenerTeamMembers eventListenerTeamMembers;
        private ProgressDialog mPrgDia;
        @Override
        protected void onDestroy()
            {
                super.onDestroy();
                mAdapetrTeamMember=null;
                mRvTeamMembers.setAdapter(null);
                mRvTeamMembers=null;
            }
        @Override
        protected void onResume()
            {
                super.onResume();
                mPrgDia.show();
                mFirebaseTeamMembers.addValueEventListener(eventListenerTeamMembers);
            } // END onResume()
        @Override
        protected void onPause()
            {
                super.onPause();
                mFirebaseTeamMembers.removeEventListener(eventListenerTeamMembers);
                mAdapetrTeamMember=null;
                mRvTeamMembers.setAdapter(null);
            }// END onPause()
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.activity_edit_syte_team_members);
                mInItObjects();
                mInItWidgets();
            }// END onCreate()
        private void mInItObjects()
            {
                layoutManager = new LinearLayoutManager(EditSyteTeamMembersActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mContext=this;
                mBun=getIntent().getExtras();
                mTeamMembers=new ArrayList<>();
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
                mFirebaseTeamMembers=new Firebase(StaticUtils.YASPAS_URL).child(mSyteId).child(StaticUtils.YASPAS_TEAM);
                eventListenerTeamMembers = new EventListenerTeamMembers();
                mPrgDia = new ProgressDialog(EditSyteTeamMembersActivity.this);
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
            }// END mInItObjects
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mTvAddTeamMember=(TextView)findViewById(R.id.xTvAddTeamMember);
                mTvAddTeamMember.setOnClickListener(this);
                mRvTeamMembers=(RecyclerView)findViewById(R.id.xRvTeamMembers);
                mRvTeamMembers.setLayoutManager(layoutManager);
            }// END mInItWidgets()
        @Override
        public void onClick(View v)
            {
                switch (v.getId())
                    {
                        case R.id.xTvAddTeamMember:
                            {
                                Intent mInt_AddSyteAddTM = new Intent(EditSyteTeamMembersActivity.this, AddSyteAddTeamMemberActivity.class);
                                Bundle mBdl = new Bundle();
                                mBdl.putParcelable(StaticUtils.IPC_TEAM_MEMBER, new YasPasTeam());
                                mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                                mInt_AddSyteAddTM.putExtras(mBdl);
                                startActivity(mInt_AddSyteAddTM);
                                break;
                            }
                        case R.id.xRelLayBack:
                            {
                                finish();
                                break;
                            }
                        default:break;
                }
            }// END onClick()
        private void mCheckAddBtnVisibility()
            {
                if(mTeamMembers.size()<5)
                    {
                        mTvAddTeamMember.setVisibility(View.VISIBLE);
                    }
                else
                    {
                        mTvAddTeamMember.setVisibility(View.GONE);
                    }
            }// END mCheckAddBtnVisibility()
        @Override
        public void onEditButtonClicked(int paramItemPosition)
            {
                Intent mInt_AddSyteAddTM = new Intent(EditSyteTeamMembersActivity.this,AddSyteEditTeamMemberActivity.class);
                Bundle mBdl = new Bundle();
                mBdl.putParcelable(StaticUtils.IPC_TEAM_MEMBER, mTeamMembers.get(paramItemPosition));
                mBdl.putInt(StaticUtils.IPC_TEAM_MEMBER_INDEX, paramItemPosition);
                mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
                mInt_AddSyteAddTM.putExtras(mBdl);
                startActivity(mInt_AddSyteAddTM);
            }// END onEditButtonClicked()
        private class EventListenerTeamMembers implements ValueEventListener
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        mTeamMembers.removeAll(mTeamMembers);
                        if(dataSnapshot.getValue()!=null)
                            {
                                Iterator<DataSnapshot> mIterator = dataSnapshot.getChildren().iterator();
                                while (mIterator.hasNext())
                                    {
                                        DataSnapshot mChildDataSnapshot = (DataSnapshot)mIterator.next();
                                        YasPasTeams yasPasTeams = new YasPasTeams();
                                        yasPasTeams.setYasPasTeamId(mChildDataSnapshot.getKey());
                                        yasPasTeams.setYasPasTeam(mChildDataSnapshot.getValue(YasPasTeam.class));
                                        mTeamMembers.add(yasPasTeams);
                                    }
                            }
                        mCheckAddBtnVisibility();
                        mAdapetrTeamMember=null;
                        mAdapetrTeamMember=new AdapterTeamMembers(mContext,mTeamMembers,((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)),EditSyteTeamMembersActivity.this);
                        mRvTeamMembers.setAdapter(mAdapetrTeamMember);
                        try{mPrgDia.dismiss();}catch (Exception e){}
                    }
                @Override
                public void onCancelled(FirebaseError firebaseError)
                    {

                    }
            }// END EventListenerTeamMembers
    }// END EditSyteTeamMembersActivity
