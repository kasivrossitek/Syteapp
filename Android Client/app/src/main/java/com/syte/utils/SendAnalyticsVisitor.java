package com.syte.utils;

import com.firebase.client.Firebase;
import com.syte.models.AnalyticsVisitor;

/**
 * Created by khalid.p on 24-03-2016.
 */
public class SendAnalyticsVisitor
    {
        private AnalyticsVisitor analyticsSyteCumBulletin;
        private Firebase firebaseTempAnalytics;
        public SendAnalyticsVisitor(AnalyticsVisitor paramAnalyticsSyteCumBulletin)
            {
                this.analyticsSyteCumBulletin=paramAnalyticsSyteCumBulletin;
                this.firebaseTempAnalytics=new Firebase(StaticUtils.TEMP_ANALYTICS_VISITOR_URL);
                SendAnalyticsData();
            }
        private void SendAnalyticsData()
            {
                firebaseTempAnalytics.push().setValue(analyticsSyteCumBulletin);
            }
    }
