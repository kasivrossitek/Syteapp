package com.syte.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by khalid.p on 05-02-2016.
 */
public class TvRobortoMedium extends TextView
{
    public TvRobortoMedium(Context context)
    {
        super(context);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf"));
    }

    public TvRobortoMedium(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf"));
    }

    public TvRobortoMedium(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Medium.ttf"));
    }

    protected void onDraw (Canvas canvas)
    {
        super.onDraw(canvas);
    }

}
