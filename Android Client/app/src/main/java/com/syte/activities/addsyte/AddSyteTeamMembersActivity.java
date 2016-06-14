package com.syte.activities.addsyte;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.syte.R;
import com.syte.adapters.AdapterTeamMembers;
import com.syte.listeners.OnEditButtonListener;
import com.syte.models.YasPasTeam;
import com.syte.models.YasPasTeams;
import com.syte.utils.StaticUtils;

import java.util.ArrayList;

/**
 * Created by khalid.p on 13-02-2016.
 */
public class AddSyteTeamMembersActivity extends Activity implements View.OnClickListener, OnEditButtonListener
    {
        private RelativeLayout mRelLayBack;
        private TextView mTvAddTeamMember;
        private RecyclerView mRvTeamMembers;
        private ArrayList<YasPasTeams> mTeamMembers;
        private AdapterTeamMembers mAdapetrTeamMember;
        private LinearLayoutManager layoutManager;
        private Context mContext;
        private Bundle mBun;
        private String mSyteId;
        @Override
        protected void onDestroy()
            {
                super.onDestroy();
                mAdapetrTeamMember=null;
                mRvTeamMembers.setAdapter(null);
                mRvTeamMembers=null;
            }
        @Override
        protected void onCreate(Bundle savedInstanceState)
            {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.acitivity_add_syte_team_members);
                mInItObjects();
                mInItWidgets();
            }// END onCreate()
        private void mInItObjects()
            {
                layoutManager = new LinearLayoutManager(AddSyteTeamMembersActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                mContext=this;
                mBun=getIntent().getExtras();
                mTeamMembers = mBun.getParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST);
                mSyteId=mBun.getString(StaticUtils.IPC_SYTE_ID);
            }// END mInItObjects
        private void mInItWidgets()
            {
                mRelLayBack=(RelativeLayout)findViewById(R.id.xRelLayBack);
                mRelLayBack.setOnClickListener(this);
                mTvAddTeamMember=(TextView)findViewById(R.id.xTvAddTeamMember);
                mTvAddTeamMember.setOnClickListener(this);
                mRvTeamMembers=(RecyclerView)findViewById(R.id.xRvTeamMembers);
                mRvTeamMembers.setLayoutManager(layoutManager);
                if(mTeamMembers.size()<5)
                    {
                        mTvAddTeamMember.setVisibility(View.VISIBLE);
                    }
                else
                    {
                        mTvAddTeamMember.setVisibility(View.GONE);
                    }
                mAdapetrTeamMember=new AdapterTeamMembers(mContext,mTeamMembers,((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)),this);
                mRvTeamMembers.setAdapter(mAdapetrTeamMember);
            }// END mInItWidgets()
        @Override
        public void onClick(View v)
            {
                switch (v.getId())
                    {
                        case R.id.xRelLayBack:
                            {
                                Intent intent = new Intent();
                                Bundle mBun1 = new Bundle();
                                mBun1.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                                intent.putExtras(mBun1);
                                setResult(2, intent);
                                finish();
                                break;
                            }
                        case R.id.xTvAddTeamMember:
                            {
                                Intent mInt_AddSyteAddTM = new Intent(AddSyteTeamMembersActivity.this,AddSyteAddTeamMemberActivity.class);
                                Bundle mBdl = new Bundle();
                                mBdl.putParcelable(StaticUtils.IPC_TEAM_MEMBER, new YasPasTeam());
                                mBdl.putString(StaticUtils.IPC_SYTE_ID,mSyteId);
                                mInt_AddSyteAddTM.putExtras(mBdl);
                                startActivityForResult(mInt_AddSyteAddTM, 1);
                                break;
                            }
                        default:break;
                    }
            }// END onClick()
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data)
            {
                super.onActivityResult(requestCode, resultCode, data);
                if(resultCode==1)
                    {
                        YasPasTeams yasPasTeam = data.getExtras().getParcelable(StaticUtils.IPC_TEAM_MEMBER);
                        mTeamMembers.add(yasPasTeam);
                        mAdapetrTeamMember.notifyDataSetChanged();
                        if(mTeamMembers.size()<5)
                            {
                                mTvAddTeamMember.setVisibility(View.VISIBLE);
                            }
                        else
                            {
                                mTvAddTeamMember.setVisibility(View.GONE);
                            }
                    }
                if(resultCode==2)
                    {
                        YasPasTeams yasPasTeam = data.getExtras().getParcelable(StaticUtils.IPC_TEAM_MEMBER);
                        if(data.getExtras().getString(StaticUtils.IPC_ACTION).equalsIgnoreCase(StaticUtils.IPC_ACTION_EDIT))
                            {
                               mTeamMembers.set(data.getExtras().getInt(StaticUtils.IPC_TEAM_MEMBER_INDEX),yasPasTeam);
                            }
                        else
                            {
                                mTeamMembers.remove(data.getExtras().getInt(StaticUtils.IPC_TEAM_MEMBER_INDEX));
                            }

                        mAdapetrTeamMember.notifyDataSetChanged();
                        if(mTeamMembers.size()<5)
                            {
                                mTvAddTeamMember.setVisibility(View.VISIBLE);
                            }
                        else
                            {
                                mTvAddTeamMember.setVisibility(View.GONE);
                            }
                    }
            }
        @Override
        public void onBackPressed()
            {
                Intent intent = new Intent();
                Bundle mBun1 = new Bundle();
                mBun1.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                intent.putExtras(mBun1);
                setResult(2, intent);
                finish();
                super.onBackPressed();
            }// END onBackPressed()

    @Override
    public void onEditButtonClicked(int paramItemPosition)
        {
            Intent mInt_AddSyteAddTM = new Intent(AddSyteTeamMembersActivity.this,AddSyteEditTeamMemberActivity.class);
            Bundle mBdl = new Bundle();
            mBdl.putParcelable(StaticUtils.IPC_TEAM_MEMBER, mTeamMembers.get(paramItemPosition));
            mBdl.putInt(StaticUtils.IPC_TEAM_MEMBER_INDEX, paramItemPosition);
            mBdl.putString(StaticUtils.IPC_SYTE_ID, mSyteId);
            mInt_AddSyteAddTM.putExtras(mBdl);
            startActivityForResult(mInt_AddSyteAddTM, 2);
        }
}// END AddSyteTeamMembersActivity
