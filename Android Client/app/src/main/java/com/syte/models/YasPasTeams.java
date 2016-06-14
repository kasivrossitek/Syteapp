package com.syte.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by khalid.p on 22-02-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class YasPasTeams implements Parcelable
    {
        private String yasPasTeamId;
        private YasPasTeam yasPasTeam;

        public YasPasTeams(){}
        protected YasPasTeams(Parcel in) {
            yasPasTeamId = in.readString();
            yasPasTeam = in.readParcelable(YasPasTeam.class.getClassLoader());
        }

        public static final Creator<YasPasTeams> CREATOR = new Creator<YasPasTeams>() {
            @Override
            public YasPasTeams createFromParcel(Parcel in) {
                return new YasPasTeams(in);
            }

            @Override
            public YasPasTeams[] newArray(int size) {
                return new YasPasTeams[size];
            }
        };

        public void setYasPasTeamId(String paramYasPasTeamId)
            {
                this.yasPasTeamId=paramYasPasTeamId;
            }
        public String getYasPasTeamId()
            {
                return this.yasPasTeamId;
            }
        public void setYasPasTeam(YasPasTeam paramYasPasTeam)
            {
                this.yasPasTeam=paramYasPasTeam;
            }
        public YasPasTeam getYasPasTeam()
            {
                return this.yasPasTeam;
            }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(yasPasTeamId);
            dest.writeParcelable(yasPasTeam, flags);
        }
    }
