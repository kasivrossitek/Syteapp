package com.syte.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.syte.R;
import com.syte.utils.StaticUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

/**
 * Created by kumar.m on 22-03-2016.
 * Modified by khalid.p on 28-03-2016 / 29-03-2016
 */
public class SyteAnalyticsDaysFragment extends Fragment {
    private View mRootView;
    private BarChart mBarChart;
    private Typeface mTf;
    private String mSyteId;
    private ArrayList<String> mXAxisLables;
    private ArrayList<Float> mVisitorData;
    private TextView mTvTotalCount;
    private ProgressDialog mPrgDia;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_syte_analytics_days, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInItObjects();
        mInItWidgets();
        //setData();
        getData();
    }// END onActivityCreated()

    private void mInItObjects() {
        mTf = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        mSyteId = getArguments().getString(StaticUtils.IPC_SYTE_ID);
        mXAxisLables = new ArrayList<>();
        mVisitorData = new ArrayList<>();
        mPrgDia = new ProgressDialog(getActivity());
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
    }// END mInItObjects()

    private void mInItWidgets() {
        mBarChart = (BarChart) mRootView.findViewById(R.id.xBarChart);
        mBarChart.getAxisRight().setDrawLabels(false); //For hiding right drawables
        mBarChart.setPinchZoom(false); // scaling can now only be done on x- and y-axis separately
        mBarChart.setScaleXEnabled(false); //For Scale X disable
        mBarChart.setScaleYEnabled(false); //For Scale Y disable
        mBarChart.setDrawBarShadow(false); //For bar shadow disable
        mBarChart.setDrawValueAboveBar(true); //For setting the value above the bar
        mBarChart.setDescription(""); //To set description
        mBarChart.setHighlightPerTapEnabled(false); //For setting bar tap highlight
        mBarChart.setHighlightPerDragEnabled(false); //For setting bar drag highlight
        mBarChart.setDrawGridBackground(false);
        mBarChart.animateXY(3000, 3000); //animate XY
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setSpaceBetweenLabels(1);
        xAxis.setTypeface(mTf);
        xAxis.setTextSize(8);
        xAxis.setTextColor(R.color.color_black_opacity_26);
        xAxis.setDrawGridLines(false);
        YAxis leftAxis = mBarChart.getAxisLeft();
        leftAxis.setLabelCount(10, false); //For setting the Y axis label count. if second param is true then it will set exactly same number of lablels otherwise it will set efficient one.
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setAxisLineColor(R.color.color_black_opacity_26);
        leftAxis.setGridColor(R.color.color_black_opacity_26);
        leftAxis.setTextColor(R.color.color_black_opacity_26);
        leftAxis.setTypeface(mTf);
        leftAxis.setAxisMinValue(0f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawZeroLine(false);
        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setDrawZeroLine(false);
        rightAxis.setAxisLineColor(Color.parseColor("#FFFFFF"));
        Legend l = mBarChart.getLegend();
        l.setEnabled(false);
        mTvTotalCount = (TextView) mRootView.findViewById(R.id.xTvTotalCount);
        mTvTotalCount.setText("0");
    }// END mInItWidgets()

    private void getData() {
        mPrgDia.show();
        new Firebase(StaticUtils.ANALYTICS_SYTE_URL).child(mSyteId).child(StaticUtils.ANALYTICS_DAILY).orderByKey().limitToFirst(7).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mInItData(dataSnapshot);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                mPrgDia.dismiss();
                mInItData(null);
            }
        });
    }// END getData()

    private void mInItData(DataSnapshot dataSnapshot) {
        float totalVisitCount = 0;
        ArrayList<String> daysShort = new ArrayList<>(Arrays.asList("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"));
        ArrayList<String> daysDates = new ArrayList<>();
        SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("yyyyMMdd");
        Date dateToday = new Date();
        for (int i = 6; i >= 0; i--) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateToday);
            cal.add(Calendar.DATE, -1 * i);
            int DY_OF_WK = cal.get(Calendar.DAY_OF_WEEK) - 1;
            Date daysBeforeDate = cal.getTime();
            String strDate = TIME_FORMAT.format(daysBeforeDate);
            String day = daysShort.get(DY_OF_WK);
            mXAxisLables.add(day);
            mVisitorData.add(0f);
            daysDates.add(strDate);
        }
        if (dataSnapshot.getValue() != null && dataSnapshot != null) {
            Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
            while (iterator.hasNext()) {
                DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                if (daysDates.contains(dataSnapshot1.getKey())) {
                    int index = daysDates.indexOf(dataSnapshot1.getKey());
                    Long totalVisitLong = (Long) dataSnapshot1.child("totalVisits").getValue();
                    float f = totalVisitLong.floatValue();
                    totalVisitCount = totalVisitCount + f;
                    mVisitorData.set(index, f);
                }
            }
        }
        mTvTotalCount.setText("" + Math.round(totalVisitCount) + "");
        setData();
    }// END mInItData()

    private void setData() {
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < mXAxisLables.size(); i++) {
            xVals.add(mXAxisLables.get(i % mXAxisLables.size()));
        }
        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        for (int i = 0; i < mVisitorData.size(); i++) {
            yVals1.add(new BarEntry(mVisitorData.get(i), i));
        }
        BarDataSet set1 = new BarDataSet(yVals1, "Visitors");
        set1.setBarSpacePercent(35f);
        set1.setValueTextColor(R.color.color_black_opacity_26);
        set1.setColor(Color.parseColor("#FFE9CC"));//For setting the bar color
        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);
        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(mTf);
        data.setValueFormatter(new MyValueFormatter());
        mPrgDia.dismiss();
        mBarChart.setData(data);
    }

    public class MyValueFormatter implements ValueFormatter {
        @Override
        public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
            return Math.round(value) + "";
        }
    }
}