package com.syte.listeners;

import com.syte.models.YasPasPush;

/**
 * Created by kumar.m on 12-03-2016.
 */
public interface OnFollowNotificationDelete
{
    void onFollowNotificationDelete(String followNotificationKey);
    void onSyteClaimNotificationClicked(YasPasPush yasPasPush,String followNotificationKey);

    void onFollowSyteInvitationClicked(YasPasPush yasPasPush,String followNotificationKey);//invite to follow by kasi on 4/6/16

}
