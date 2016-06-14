package com.syte.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.EditText;

import com.syte.R;

/**
 * Created by khalid.p on 06-02-2016.
 */
public class EtRobortoRegular extends EditText
    {

        public EtRobortoRegular(Context context)
            {
                super(context);
                // TODO Auto-generated constructor stub
                this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.edittext_txt_sz));
                if (Build.VERSION.SDK_INT >= 23)
                    {
                        this.setHintTextColor(ContextCompat.getColor(context, R.color.color_edit_hint));
                        this.setTextColor(ContextCompat.getColor(context, R.color.color_edit_text));
                        this.setBackgroundResource(R.drawable.background_edittext);
                    }
                else
                    {
                        this.setHintTextColor(context.getResources().getColor(R.color.color_edit_hint));
                        this.setTextColor(context.getResources().getColor(R.color.color_edit_text));
                        this.setBackgroundResource(R.drawable.background_edittext);
                    }
            }
        public EtRobortoRegular(Context context, AttributeSet attrs)
            {
                super(context, attrs);
                this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.edittext_txt_sz));
                if (Build.VERSION.SDK_INT >= 23)
                {
                    this.setHintTextColor(ContextCompat.getColor(context, R.color.color_edit_hint));
                    this.setTextColor(ContextCompat.getColor(context, R.color.color_edit_text));
                    this.setBackgroundResource(R.drawable.background_edittext);
                }
                else
                {
                    this.setHintTextColor(context.getResources().getColor(R.color.color_edit_hint));
                    this.setTextColor(context.getResources().getColor(R.color.color_edit_text));
                    this.setBackgroundResource(R.drawable.background_edittext);
                }
            }

        public EtRobortoRegular(Context context, AttributeSet attrs, int defStyle)
            {
                super(context, attrs, defStyle);
                this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Regular.ttf"));
                this.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.edittext_txt_sz));
                if (Build.VERSION.SDK_INT >= 23)
                    {
                        this.setHintTextColor(ContextCompat.getColor(context, R.color.color_edit_hint));
                        this.setTextColor(ContextCompat.getColor(context, R.color.color_edit_text));
                        this.setBackgroundResource(R.drawable.background_edittext);
                    }
                else
                    {
                        this.setHintTextColor(context.getResources().getColor(R.color.color_edit_hint));
                        this.setTextColor(context.getResources().getColor(R.color.color_edit_text));
                        this.setBackgroundResource(R.drawable.background_edittext);
                    }
            }
    }
