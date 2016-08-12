package com.syte.listeners;

import com.syte.models.BulletinBoard;

/**
 * Created by khalid.p on 29-02-2016.
 */
public interface OnBulletinBoardUpdate
    {
        public void onBulletinEdit(String paramBulletinId,BulletinBoard paramBulletinBoard);
        public void onBulletinDelete(String paramBulletinId);
        public void onBulletinReadMore(int paramPosition);

    }
