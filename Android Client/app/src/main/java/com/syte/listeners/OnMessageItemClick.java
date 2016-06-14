package com.syte.listeners;

import com.syte.models.Message;
import com.syte.models.SponsorInnerMessage;

/**
 * Created by khalid.p on 14-03-2016.
 */
public interface OnMessageItemClick
    {
        public void onMessageItemClicked(Message yasPaseeChat);
        public void onSponsorInnerMessageItemClciked(SponsorInnerMessage sponsorInnerMessage);
    }
