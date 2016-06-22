package com.syte.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.syte.R;
import com.syte.adapters.AdapterSyteDetalTeamMembers;
import com.syte.models.YasPasTeam;

import java.util.ArrayList;

/**
 * Created by khalid.p on 24-02-2016.
 */
public class SyteDetailTeamMembersFragment extends Fragment
    {
        private View mRootView;
        private RecyclerView mRvTeamMembers;
        private LinearLayoutManager layoutManager;
        private AdapterSyteDetalTeamMembers adapterSyteDetalTeamMembers;
        private ArrayList<YasPasTeam> yasPasTeams;
        private RelativeLayout mRelLayNoDetails;
        public SyteDetailTeamMembersFragment(){}
        @Override
        public void onDestroy()
            {
                super.onDestroy();
                yasPasTeams=null;
                adapterSyteDetalTeamMembers=null;
                mRvTeamMembers.setAdapter(null);
                mRvTeamMembers=null;
            }
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
            {
                // Inflate the layout for this fragment
                mRootView = inflater.inflate(R.layout.fragment_syte_detail_team_members, container, false);
                return mRootView;
            }
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
            {
                super.onActivityCreated(savedInstanceState);
                mInItObjects();
                mInItWidgets();
                mRvTeamMembers.setAdapter(adapterSyteDetalTeamMembers);
            }// END onActivityCreated()
        private void mInItObjects()
            {
                layoutManager = new LinearLayoutManager(getActivity());
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                yasPasTeams=new ArrayList<>();
                adapterSyteDetalTeamMembers=new AdapterSyteDetalTeamMembers(getActivity(),yasPasTeams,((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)));

            }//END mInItObjects()
        private void mInItWidgets()
            {
                mRvTeamMembers=(RecyclerView)mRootView.findViewById(R.id.xRvTeamMembers);
                mRvTeamMembers.setLayoutManager(layoutManager);
                mRelLayNoDetails=(RelativeLayout)mRootView.findViewById(R.id.xRelLayNoDetails);

            }// END mInItWidgets()
        public void sUpdateTeamMember(ArrayList<YasPasTeam> paramYasPasTeams)
            {
                yasPasTeams.removeAll(yasPasTeams);
                yasPasTeams=paramYasPasTeams;
                adapterSyteDetalTeamMembers=new AdapterSyteDetalTeamMembers(getActivity(),yasPasTeams,((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)));
                mRvTeamMembers.setAdapter(adapterSyteDetalTeamMembers);
                if(yasPasTeams.size()<=0)
                    {
                        mRelLayNoDetails.setVisibility(View.VISIBLE);
                    }
                else
                    {
                        mRelLayNoDetails.setVisibility(View.GONE);
                    }
            }

    }
