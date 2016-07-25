package com.syte.widgets;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;

import com.syte.R;
import com.syte.listeners.OnCustomDialogsListener;

/**
 * Created by khalid.p on 04-02-2016.
 */
public class CustomDialogs {
    private static CustomDialogs CUSTOM_DIALOGS = null;
    private static Dialog DIALOG = null;
    private OnCustomDialogsListener mObjListener;
    public static int D_TYPE_COMMON = 1;

    private CustomDialogs(Context paramCon, OnCustomDialogsListener paramListener) {
        DIALOG = new Dialog(paramCon);
        DIALOG.requestWindowFeature(Window.FEATURE_NO_TITLE);
        DIALOG.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        DIALOG.setCancelable(false);
        DIALOG.setCanceledOnTouchOutside(false);
        this.mObjListener = paramListener;
    }

    public static CustomDialogs CREATE_DIALOG(Context paramCon, OnCustomDialogsListener paramListener) {
        if (DIALOG != null)
            DIALOG = null;
        if (CUSTOM_DIALOGS != null) {
            CUSTOM_DIALOGS = null;
        }
        CUSTOM_DIALOGS = new CustomDialogs(paramCon, paramListener);
        return CUSTOM_DIALOGS;
    }

    public void sShowDialog_Common(String paramSubject, String paramHeading, String paramBody, String paramLeftBtnLbl, final String paramRghtBtnLbl, final String paramCallingMethod, final boolean paramIsLftFnsh, final boolean paramIsRghtFnsh) {
        DIALOG.setContentView(R.layout.layout_dialog);
        DIALOG.show();
        Window window = DIALOG.getWindow();
        com.syte.widgets.TvRobortoMedium mTvSubject = (com.syte.widgets.TvRobortoMedium) window.findViewById(R.id.xTvSubject);
        com.syte.widgets.TvRobortoRegular mTvHeading = (com.syte.widgets.TvRobortoRegular) window.findViewById(R.id.xTvHeading);
        com.syte.widgets.TvRobortoRegular mTvBody = (com.syte.widgets.TvRobortoRegular) window.findViewById(R.id.xTvBody);
        com.syte.widgets.TvRobortoMedium mTvLeft = (com.syte.widgets.TvRobortoMedium) window.findViewById(R.id.xTvLeft);
        com.syte.widgets.TvRobortoMedium mTvRight = (com.syte.widgets.TvRobortoMedium) window.findViewById(R.id.xTvRight);
        if (paramSubject == null) {
            mTvSubject.setVisibility(View.GONE);
        } else {
            mTvSubject.setText(paramSubject);
        }
        if (paramHeading == null) {
            mTvHeading.setVisibility(View.GONE);
        } else {
            mTvHeading.setText(paramHeading);
        }
        if (paramBody == null) {
            mTvBody.setVisibility(View.GONE);
        } else {
            mTvBody.setText(paramBody);
        }

        if (paramLeftBtnLbl == null) {
            mTvLeft.setVisibility(View.GONE);
        } else {
            mTvLeft.setText(paramLeftBtnLbl);
            mTvLeft.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DIALOG.dismiss();
                    mObjListener.onDialogLeftBtnClicked(D_TYPE_COMMON, paramCallingMethod, paramIsLftFnsh);
                }
            });
        }
        if (paramRghtBtnLbl == null) {
            mTvRight.setVisibility(View.GONE);
        } else {
            mTvRight.setText(paramRghtBtnLbl);
            mTvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DIALOG.dismiss();
                    mObjListener.onDialogRightBtnClicked(D_TYPE_COMMON, paramCallingMethod, paramIsRghtFnsh);
                }
            });
        }
    } // END sShowDialog_1()

    public void sShowDialog_Common(int view) {
        DIALOG.setContentView(view);
        DIALOG.show();
    }


}
