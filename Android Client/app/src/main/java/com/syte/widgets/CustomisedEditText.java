package com.syte.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * Created by khalid.p on 04-02-2016.
 */
public class CustomisedEditText extends EditText
    {

        public CustomisedEditText(Context context)
            {
                super(context);
                // TODO Auto-generated constructor stub
                //this.setTypeface(CustomisedFont.GET_CUSTOMISED_FONT(context));
            }
        public CustomisedEditText(Context context, AttributeSet attrs)
            {
                super(context, attrs);
                //this.setTypeface(CustomisedFont.GET_CUSTOMISED_FONT(context));
            }

        public CustomisedEditText(Context context, AttributeSet attrs, int defStyle)
            {
                super(context, attrs, defStyle);
                //this.setTypeface(CustomisedFont.GET_CUSTOMISED_FONT(context));
            }

    }
