package com.syte.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;

import com.syte.R;

/**
 * Created by khalid.p on 13-02-2016.
 */
public class EditTextLblMedium extends TextView
    {
        public EditTextLblMedium(Context context)
        {
            super(context);
            this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.edittext_medium_lbl_sz));
            this.setPadding(0,0,0,10);
            if (Build.VERSION.SDK_INT >= 23)
            {
                this.setTextColor(ContextCompat.getColor(context, R.color.color_edit_lbl));
            }
            else
            {
                this.setTextColor(context.getResources().getColor(R.color.color_edit_lbl));
            }
        }

        public EditTextLblMedium(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.edittext_medium_lbl_sz));
            this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));
            this.setPadding(0,0,0,10);
            if (Build.VERSION.SDK_INT >= 23)
            {
                this.setTextColor(ContextCompat.getColor(context, R.color.color_edit_lbl));
            }
            else
            {
                this.setTextColor(context.getResources().getColor(R.color.color_edit_lbl));
            }
        }

        public EditTextLblMedium(Context context, AttributeSet attrs, int defStyle)
        {
            super(context, attrs, defStyle);
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.edittext_medium_lbl_sz));
            this.setPadding(0,0,0,10);
            this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));
            if (Build.VERSION.SDK_INT >= 23)
            {
                this.setTextColor(ContextCompat.getColor(context, R.color.color_edit_lbl));
            }
            else
            {
                this.setTextColor(context.getResources().getColor(R.color.color_edit_lbl));
            }
        }

        protected void onDraw (Canvas canvas)
        {
            super.onDraw(canvas);
        }
    }
