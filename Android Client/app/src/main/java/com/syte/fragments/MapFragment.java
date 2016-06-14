package com.syte.fragments;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.syte.R;
import com.syte.activities.HomeActivity;
import com.syte.activities.addsyte.AddSyteLocationActivity;
import com.syte.activities.sytedetailsponsor.SyteDetailSponsorActivity;
import com.syte.activities.sytedetailuser.SyteDetailUserActivity;
import com.syte.listeners.OnCustomDialogsListener;
import com.syte.models.CustomMapMarker;
import com.syte.models.Syte;
import com.syte.models.YasPasTeams;
import com.syte.utils.NetworkStatus;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasMessages;
import com.syte.utils.YasPasPreferences;
import com.syte.widgets.CustomDialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khalid.p on 09-02-2016.
 */
public class MapFragment extends Fragment implements GoogleMap.OnCameraChangeListener, GeoQueryEventListener, GoogleMap.OnInfoWindowClickListener, View.OnClickListener, OnCustomDialogsListener, GoogleMap.OnMarkerClickListener
{
        private View mRootView;
        //private Bundle mBun;
        private GoogleMap mMap;
        private GeoFire geoFire;
        private GeoQuery geoQuery;
        private YasPasPreferences mYasPasPref;
        private Map<String,CustomMapMarker> mMapMarkers;
        private LinearLayout mLinLaySearchBox;
        private float animationDistance;
        private EditText mEtSearchInput;
        private InputMethodManager mInputManager;
        private FloatingActionButton mFabCurrentLoc,mFabAddYasPas;
        private NetworkStatus mNetworkStatus;
        private Marker mCurrentLocationMarker;
        private String mCurrentLocationMarkerKey = "-1mCurrentLocationMarkerKey1-";
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
            {
                mRootView = inflater.inflate(R.layout.fragment_map, container, false);
                return mRootView;
            }// END onCreateView()
        @Override
        public void onActivityCreated(Bundle savedInstanceState)
            {
                super.onActivityCreated(savedInstanceState);
                mInItObjects();
                mInItWidgets();
            }// END onActivityCreated()
        private void mInItObjects()
            {
                //mBun=getArguments();
                mYasPasPref = YasPasPreferences.GET_INSTANCE(getActivity());
                mMapMarkers=new HashMap<String, CustomMapMarker>();
                animationDistance=getResources().getDimension(R.dimen.map_screen_search_box_margin);
                mInputManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mNetworkStatus=new NetworkStatus(getActivity());
            }// END mInItObjects()
        private void mInItWidgets()
            {
                mLinLaySearchBox=(LinearLayout)mRootView.findViewById(R.id.xLinLaySearchBox);
                mLinLaySearchBox.setY(-animationDistance - mLinLaySearchBox.getHeight());
                mEtSearchInput=(EditText)mRootView.findViewById(R.id.xEtSearchInput);
                mEtSearchInput.addTextChangedListener(mSearchBoxTextWatcher);
                mFabCurrentLoc=(FloatingActionButton)mRootView.findViewById(R.id.xFabCurrentLoc);
                mFabCurrentLoc.setOnClickListener(this);
                mFabAddYasPas=(FloatingActionButton)mRootView.findViewById(R.id.xFabAddYasPas);
                mFabAddYasPas.setOnClickListener(this);
                //mFabAddYasPas
            }// END mInItWidgets()
        @Override
        public void onResume()
            {
                super.onResume();
                setUpMapIfNeeded();
                LatLng latLng = new LatLng(HomeActivity.LOC_VIRTUAL.getLatitude(),HomeActivity.LOC_VIRTUAL.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, HomeActivity.ZOOM_LEVEL);
                mMap.moveCamera(cameraUpdate);

                geoFire = new GeoFire(new Firebase(StaticUtils.YASPAS_GEO_LOC_URL));
                geoQuery = this.geoFire.queryAtLocation(new GeoLocation(HomeActivity.LOC_VIRTUAL.getLatitude(),HomeActivity.LOC_VIRTUAL.getLongitude()), 10);
                this.geoQuery.addGeoQueryEventListener(this);

                sSetCurrentLocation(HomeActivity.LOC_ACTUAL.getLatitude(),HomeActivity.LOC_ACTUAL.getLongitude());
            }// END onResume()
        private void setUpMapIfNeeded()
            {
                if (mMap == null)
                    {
                        mMap = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map)).getMap();
                        mMap.setMyLocationEnabled(false);
                        mMap.setOnInfoWindowClickListener(this);
                        mMap.setOnCameraChangeListener(this);
                        if(mMap!=null)
                            {
                                mMap.setInfoWindowAdapter(new LayoutMapPinInfoWindow());
                                mMap.setOnMarkerClickListener(this);
                            }
                    }
            }// END setUpMapIfNeeded()
        @Override
        public void onPause()
            {
                super.onPause();
                // remove all event listeners to stop updating in the background
                geoQuery.removeAllListeners();
                for (CustomMapMarker value : mMapMarkers.values())
                {
                    Marker m = value.marker;
                    m.remove();
                }
                this.mMapMarkers.clear();
                if(mCurrentLocationMarker!=null)
                {
                    mCurrentLocationMarker.remove();
                }
            }// END onPause()
        @Override
        public void onDestroy()
            {
                super.onDestroy();
            }// END onDestroy()

        @Override
        public void onStop()
            {
                super.onStop();

        }

        @Override
        public void onCameraChange(CameraPosition cameraPosition)
            {
                // Update the search criteria for this geoQuery and the circle on the map
                LatLng center = cameraPosition.target;
                HomeActivity.LOC_VIRTUAL.setLatitude(center.latitude);
                HomeActivity.LOC_VIRTUAL.setLongitude(center.longitude);
                HomeActivity.ZOOM_LEVEL=cameraPosition.zoom;
                double radius = zoomLevelToRadius(cameraPosition.zoom);
                try{
                    this.geoQuery.setCenter(new GeoLocation(center.latitude, center.longitude));
                    this.geoQuery.setRadius(radius / 400);}
                catch (Exception e){}
            } // END onCameraChange()
        @Override
        public void onKeyEntered(final String key, final GeoLocation location)
            {
                Firebase obj = new Firebase(StaticUtils.YASPAS_URL).child(key);
                obj.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if(dataSnapshot.getValue()!=null){
                                boolean isOwnedYasPas=false;
                                MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(location.latitude, location.longitude));
                                markerOptions.title(key);
                                if (dataSnapshot.child("owner").getValue().toString().equalsIgnoreCase(mYasPasPref.sGetRegisteredNum()))
                                    {
                                        isOwnedYasPas = true;
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_orange_pin));
                                    }
                                else if(!isOwnedYasPas && dataSnapshot.child(StaticUtils.YASPAS_MANAGERS).hasChild(mYasPasPref.sGetRegisteredNum()))
                                    {
                                        isOwnedYasPas=true;
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_orange_pin));
                                    }
                                else
                                    {
                                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_green_pin));
                                    }
                                Marker marker = mMap.addMarker(markerOptions);
                                marker.setVisible(false);
                                CustomMapMarker customMapMarker = new CustomMapMarker(marker,
                                        dataSnapshot.child("name").getValue().toString(),
                                        dataSnapshot.child("latestBulletin").getValue().toString(),
                                        dataSnapshot.child("imageUrl").getValue().toString(),
                                        isOwnedYasPas,
                                        dataSnapshot.child("category").getValue().toString().trim(),
                                        isOwnedYasPas ? true : (dataSnapshot.child(StaticUtils.FOLLOWERS_YASPAS).hasChild(mYasPasPref.sGetRegisteredNum())),dataSnapshot.child("city").getValue().toString());
                                mMapMarkers.put(key, customMapMarker);
                                    //marker.setVisible(true);
                                checkMarkerVisibility(customMapMarker);}
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {

                            }
                    });
            }// END onKeyEntered()
        //@Override
        /*public void onKeyEntered(final String key, final GeoLocation location)
            {
                Firebase obj = new Firebase(StaticUtils.YASPAS_URL).child(key);
                obj.addListenerForSingleValueEvent(new ValueEventListener()
                    {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot)
                            {
                                final boolean isOwnedYasPas1;
                                final MarkerOptions markerOptions = new MarkerOptions();
                                markerOptions.position(new LatLng(location.latitude, location.longitude));
                                markerOptions.title(key);
                                if (dataSnapshot.child("owner").getValue().toString().equalsIgnoreCase(mYasPasPref.sGetRegisteredNum()))
                                    isOwnedYasPas1 = true;
                                else
                                    isOwnedYasPas1 = false;
                                Firebase ref = new Firebase(StaticUtils.YASPAS_URL).child(key).child(StaticUtils.YASPAS_MANAGERS);
                                Query queryRef = ref.orderByChild("managerId").equalTo(mYasPasPref.sGetRegisteredNum());
                                queryRef.addListenerForSingleValueEvent(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot1)
                                            {
                                                boolean isOwnedYasPas2;
                                                isOwnedYasPas2 = false;
                                                if (dataSnapshot1.getValue() != null)
                                                    {
                                                        isOwnedYasPas2 = true;
                                                    }
                                                if (isOwnedYasPas1 || isOwnedYasPas2)
                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_orange_pin));
                                                else
                                                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home_green_pin));
                                                final Marker marker = mMap.addMarker(markerOptions);
                                                final CustomMapMarker customMapMarker = new CustomMapMarker(marker,
                                                dataSnapshot.child("name").getValue().toString(),
                                                dataSnapshot.child("latestBulletin").getValue().toString(),
                                                dataSnapshot.child("imageUrl").getValue().toString(),
                                                isOwnedYasPas1 || isOwnedYasPas2,
                                                dataSnapshot.child("category").getValue().toString().trim(),
                                                (isOwnedYasPas1 || isOwnedYasPas2) ? true : (dataSnapshot.child(StaticUtils.FOLLOWERS_YASPAS).hasChild(mYasPasPref.sGetRegisteredNum())));
                                                mMapMarkers.put(key, customMapMarker);
                                                checkMarkerVisibility(customMapMarker);
                                            }
                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {}
                                    });
                            }
                        @Override
                        public void onCancelled(FirebaseError firebaseError)
                            {
                            }
                    });
            } */// END onKeyEntered()

    @Override
    public void onKeyExited(String key) {

            Log.e("onKeyExited", "" + key);
            try {
                Marker marker = mMapMarkers.get(key).marker;
                if (marker != null) {
                    marker.remove();
                    mMapMarkers.remove(key);
                }
            }
            catch (Exception e){}

        }

        @Override
        public void onKeyMoved(String key, GeoLocation location) {

            Marker marker = mMapMarkers.get(key).marker;//this.markers.get(key);

            if (marker != null)
            {
                animateMarkerTo(marker, location.latitude, location.longitude);
            }

        }

        @Override
        public void onGeoQueryReady() {

        }

        @Override
        public void onGeoQueryError(FirebaseError error) {

        }
        private double zoomLevelToRadius(double zoomLevel){
            // Approximation to fit circle into view
            return 16384000/Math.pow(2, zoomLevel);
        }
        public  void sCHECK_MARKER_VISIBILITY()
            {
                for (Map.Entry<String, CustomMapMarker> entry:  mMapMarkers.entrySet()) {
                    CustomMapMarker cMarker = entry.getValue();
                    checkMarkerVisibility(cMarker);
                }
            }
        private  void checkMarkerVisibility(CustomMapMarker cMarker)
        {
            String searchStr = mEtSearchInput.getText().toString();
            if ((!containsIgnoreCase(cMarker.getTitle(), searchStr)) && (!containsIgnoreCase(cMarker.getCategories(), searchStr)))
            {
                cMarker.getMarker().setVisible(false);
                cMarker.setVisible(false);
            }
            else
            {
                if(HomeActivity.IS_FAVOURITE_CHECKED)
                {
                    if(cMarker.isFollowingYasPas)
                    {
                        cMarker.getMarker().setVisible(true);
                        cMarker.setVisible(true);
                    }
                    else
                    {
                        cMarker.getMarker().setVisible(false);
                        cMarker.setVisible(false);
                    }
                }
                else
                {
                    cMarker.getMarker().setVisible(true);
                    cMarker.setVisible(true);
                }
            }
        }

        private boolean containsIgnoreCase( String haystack, String needle ) {
            if(needle.equals(""))
                return true;
            if(haystack == null || needle == null || haystack .equals(""))
                return false;

            Pattern p = Pattern.compile(needle,Pattern.CASE_INSENSITIVE+Pattern.LITERAL);
            Matcher m = p.matcher(haystack);
            return m.find();
        }

        private void animateMarkerTo(final Marker marker, final double lat, final double lng)
        {
            final Handler handler = new Handler();
            final long start = SystemClock.uptimeMillis();
            final long DURATION_MS = 3000;
            final Interpolator interpolator = new AccelerateDecelerateInterpolator();
            final LatLng startPosition = marker.getPosition();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    float elapsed = SystemClock.uptimeMillis() - start;
                    float t = elapsed / DURATION_MS;
                    float v = interpolator.getInterpolation(t);

                    double currentLat = (lat - startPosition.latitude) * v + startPosition.latitude;
                    double currentLng = (lng - startPosition.longitude) * v + startPosition.longitude;
                    marker.setPosition(new LatLng(currentLat, currentLng));
                    // if animation is not finished yet, repeat
                    if (t < 1) {
                        handler.postDelayed(this, 16);
                    }
                }
            });
        }// END animateMarkerTo()
        public void sAnimateSearchBox()
            {

                mEtSearchInput.setText("");
                if(mLinLaySearchBox.getVisibility()==View.VISIBLE)
                    {
                        /*mIvTemp.animate().translationY(-500).setDuration(3000).alphaBy(1.0f).alpha(0.0f)*/
                        mLinLaySearchBox.animate().translationYBy(-animationDistance - mLinLaySearchBox.getHeight()).setDuration(800)
                                .setListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mLinLaySearchBox.setVisibility(View.GONE);
                                        mEtSearchInput.setText("");
                                        StaticUtils.REMOVE_FOCUS(mEtSearchInput, mInputManager);
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }
                else
                    {
                        mLinLaySearchBox.setVisibility(View.VISIBLE);
                        mLinLaySearchBox.setY(-mLinLaySearchBox.getHeight());
                        mLinLaySearchBox.animate().translationYBy(animationDistance+mLinLaySearchBox.getHeight()).setDuration(800).setListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                mLinLaySearchBox.setVisibility(View.VISIBLE);
                                mEtSearchInput.setText("");
                                StaticUtils.REQUEST_FOCUS(mEtSearchInput,mInputManager);
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                    }
            }

        @Override
        public void onInfoWindowClick(Marker marker)
            {
                if(!marker.getTitle().equalsIgnoreCase(mCurrentLocationMarkerKey))
                    {
                if(mNetworkStatus.isNetworkAvailable())
                    {
                        if(!mMapMarkers.get(marker.getTitle()).isOwnedYasPas)
                            {
                                Intent mIntSyteDetailUser = new Intent(getActivity(), SyteDetailUserActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(StaticUtils.IPC_SYTE_ID, marker.getTitle());
                                bundle.putBoolean(StaticUtils.IPC_FOLLOWED_SYTE,mMapMarkers.get(marker.getTitle()).isFollowingYasPas);
                                mIntSyteDetailUser.putExtras(bundle);
                                startActivity(mIntSyteDetailUser);
                            }
                        else
                            {
                                Intent mIntSyteDetailSponsor = new Intent(getActivity(), SyteDetailSponsorActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString(StaticUtils.IPC_SYTE_ID, marker.getTitle());
                                mIntSyteDetailSponsor.putExtras(bundle);
                                startActivity(mIntSyteDetailSponsor);
                            }
                    }
                else
                    {
                        CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(),this);
                        customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                    }
                    }
            }// END onInfoWindowClick()
        // TextWatcher for Serach Box
        private final TextWatcher mSearchBoxTextWatcher = new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.e("beforeTextChanged","beforeTextChanged");
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Log.e("onTextChanged","onTextChanged");
            }

            public void afterTextChanged(Editable s) {
                //Log.e("afterTextChanged","afterTextChanged");
                for (Map.Entry<String, CustomMapMarker> entry:  mMapMarkers.entrySet()) {
                    CustomMapMarker cMarker = entry.getValue();
                    checkMarkerVisibility(cMarker);
                }
            }

        };

    @Override
    public void onClick(View v)
        {
            switch (v.getId())
                {
                    case R.id.xFabCurrentLoc:
                        {
                            /*Resetting Virtual location as Actual location
                            * Resetting Zoom level as 22
                            * Animating camera back to the user's current/actual location*/
                            HomeActivity.LOC_VIRTUAL.setLatitude(HomeActivity.LOC_ACTUAL.getLatitude());
                            HomeActivity.LOC_VIRTUAL.setLongitude(HomeActivity.LOC_ACTUAL.getLongitude());
                            HomeActivity.ZOOM_LEVEL=StaticUtils.MAP_DEFAULT_ZOOM_LEVEL;
                            CameraPosition.Builder camPosBuilder = new CameraPosition.Builder();
                            camPosBuilder.zoom(HomeActivity.ZOOM_LEVEL);
                            camPosBuilder.target(new LatLng(HomeActivity.LOC_VIRTUAL.getLatitude(), HomeActivity.LOC_VIRTUAL.getLongitude()));
                            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPosBuilder.build()));
                            break;
                        }
                    case R.id.xFabAddYasPas:
                        {
                            CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(),this);
                            customDialogs.sShowDialog_Common(YasPasMessages.ADD_YASPAS_SUBJECT, YasPasMessages.ADD_YASPAS_HEADING, null, "NO", "YES", "CreateSyte", false, false);
                            break;
                        }
                    default:break;
                }
        }
    private void mAddYasPas()
        {
            if(mNetworkStatus.isNetworkAvailable())
                {
                    Intent mInt_AddSyteLoc = new Intent(getActivity(), AddSyteLocationActivity.class);
                    Bundle mBun=new Bundle();

                    Syte mSyte = new Syte();

                    mSyte.setLatitude(HomeActivity.LOC_VIRTUAL.getLatitude());
                    mSyte.setLongitude(HomeActivity.LOC_VIRTUAL.getLongitude());
                    mSyte.setImageUrl("");

                    ArrayList<YasPasTeams> mTeamMembers = new ArrayList<>();
                    mBun.putParcelable(StaticUtils.IPC_SYTE, mSyte);
                    mBun.putString(StaticUtils.IPC_SYTE_ID,"");
                    mBun.putParcelableArrayList(StaticUtils.IPC_TEAM_MEMBERS_LIST, mTeamMembers);
                    mInt_AddSyteLoc.putExtras(mBun);
                    startActivity(mInt_AddSyteLoc);
                }
            else
                {
                    CustomDialogs customDialogs = CustomDialogs.CREATE_DIALOG(getActivity(),this);
                    customDialogs.sShowDialog_Common(YasPasMessages.NO_INTERNET_SUBJECT, YasPasMessages.NO_INTERNET_HEADING, YasPasMessages.NO_INTERNET_BODY, "NO", "YES", "NoNw", true, false);
                }
        }// END mAddYasPas()

    @Override
    public void onDialogLeftBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
        {
            if(paramDialogType==CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw") && paramIsFinish)
                {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(startMain);
                    getActivity().finish();
                }
        }

    @Override
    public void onDialogRightBtnClicked(int paramDialogType, String paramCallingMethod, boolean paramIsFinish)
        {
            if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("NoNw"))
                {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            if(paramDialogType== CustomDialogs.D_TYPE_COMMON && paramCallingMethod.equalsIgnoreCase("CreateSyte"))
                {
                    mAddYasPas();
                }
        }

    @Override
    public boolean onMarkerClick(Marker marker)
        {
            if(marker.getTitle().equalsIgnoreCase(mCurrentLocationMarkerKey))
                return true;
            else
                return false;
        }

    public class LayoutMapPinInfoWindow implements GoogleMap.InfoWindowAdapter
        {
            private View viewPinInfoWindow;
            public LayoutMapPinInfoWindow()
                {
                    viewPinInfoWindow = getActivity().getLayoutInflater().inflate(R.layout.testing1, null);
                }
            @Override
            public View getInfoContents(Marker marker)
                {
                    return null;
                }
            @Override
            public View getInfoWindow(final Marker marker)
                {
                    TextView mTvYasPasName=(TextView) viewPinInfoWindow.findViewById(R.id.xTvYasPasName);
                    TextView mTvLatestBullentinSub=(TextView) viewPinInfoWindow.findViewById(R.id.xTvLatestBullentinSub);
                    TextView mTvLatestBullentinLbl =(TextView) viewPinInfoWindow.findViewById(R.id.xTvLatestBullentinLbl);
                    if(marker.getTitle().equalsIgnoreCase(mCurrentLocationMarkerKey))
                        {
                            mTvYasPasName.setText(getString(R.string.map_screen_page_title));
                            mTvLatestBullentinSub.setVisibility(View.GONE);
                        }
                    else
                        {
                            CustomMapMarker m = mMapMarkers.get(marker.getTitle());
                            mTvYasPasName.setText(m.title);
                            if(m.bulletin.toString().trim().length()>0)
                                {
                                    mTvLatestBullentinLbl.setText(getString(R.string.map_screen_pin_info_box_recent_bulletin));
                                    mTvLatestBullentinSub.setText(m.bulletin);
                                }
                            else
                                {
                                    mTvLatestBullentinLbl.setText(getString(R.string.map_screen_pin_info_box_no_recent_bulletin));
                                    mTvLatestBullentinSub.setText("");
                                }
                        }

                    return viewPinInfoWindow;
                }
        }// END CustomMapPinInfoWindow
    public void sSetCurrentLocation(double paramLat, double paramLong)
        {
            if(mCurrentLocationMarker!=null)
                {
                    mCurrentLocationMarker.remove();
                }
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(paramLat, paramLong));
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_map_current_location));
            markerOptions.title(mCurrentLocationMarkerKey);
            mCurrentLocationMarker = mMap.addMarker(markerOptions);
        }// END sSetCurrentLocation()

    }// END MapFragment
