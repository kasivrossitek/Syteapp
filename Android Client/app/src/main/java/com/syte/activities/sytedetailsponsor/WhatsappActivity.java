package com.syte.activities.sytedetailsponsor;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.adapters.AdapterPhoneContacts;
import com.syte.adapters.AdapterWhatsapp;
import com.syte.models.AuthDb;
import com.syte.models.Followers;
import com.syte.models.PhoneContact;
import com.syte.models.Syte;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by kasi.v on 24-05-2016.
 */
public class WhatsappActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout mRelLayWABack, mRelaysearch;
    private ImageView msearch_close;
    private LinearLayout mLinLaySearchBox;
    private EditText mEtSearchContact;
    ;
    private RecyclerView mRvWAContacts;
    private LinearLayoutManager layoutManager;
    private Context mContext;
    private ProgressDialog mPrgDia;
    private TextView mTvWACenterLbl;
    private AdapterWhatsapp adapterWA;

    Bundle mBun;
    private String mySyteId;
    private Syte mySyte;
    private YasPasPreferences mYasPasPref;
    ArrayList<Followers> mFollower;
    ArrayList<String> Authored_numbers;
    ArrayList<String> myWAContacts_id, mInvited_numbers;
    ArrayList<Bitmap> myWAContacts_photo;
    ArrayList<PhoneContact> myWAContacts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whatsapp);
        mInItObjects();
        mInItWidgets();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onStart() {
        super.onStart();

        mPrgDia.show();
        if(mLinLaySearchBox.getVisibility()==View.VISIBLE){
            mLinLaySearchBox.setVisibility(View.GONE);
            mRelLayWABack.setVisibility(View.VISIBLE);
            mRelaysearch.setVisibility(View.VISIBLE);
        }
        // getAuthorisedNumbers();
        mGetWhatsAppContacts();
    }


    private void mInvites() {
        final Firebase firebaseUpdateInviteStatus = new Firebase(StaticUtils.YASPAS_URL).child(mySyteId).child("invitetofollow");

        firebaseUpdateInviteStatus.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mInvited_numbers.size() != 0) {
                    mInvited_numbers.removeAll(mInvited_numbers);
                }
                if (dataSnapshot.getValue() != null) {

                    Iterator<DataSnapshot> iteratorFollowers = dataSnapshot.getChildren().iterator();
                    while (iteratorFollowers.hasNext()) {
                        DataSnapshot dataSnapshotFollower = (DataSnapshot) iteratorFollowers.next();
                        String invited_key = dataSnapshotFollower.getKey();
                        Log.d("invitetofollow data", invited_key);
                        mInvited_numbers.add(invited_key);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(" firebaseError", firebaseError.getMessage());
            }
        });

    }

    private void mGetWhatsAppContacts() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                                /*Checking if there is any image for this team member
                                * if YES then deleting the image from cloudinary*/

                mInvites();
                String str = readWhatsappContacts();
                return str;
            }

            @Override
            protected void onPreExecute() {
                mPrgDia.setMessage(getString(R.string.prg_bar_wait));
                mPrgDia.setCancelable(false);
                mPrgDia.show();
            }

            @Override
            protected void onPostExecute(String result) {
                Log.d("", result);
                mPrgDia.dismiss();
                adapterWA = new AdapterWhatsapp(myWAContacts_id, mFollower, Authored_numbers, myWAContacts, mySyte, mySyteId, mYasPasPref, mInvited_numbers, ((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)), getApplicationContext());
                mRvWAContacts.setAdapter(adapterWA);
            }
        }.execute();
    } // END mGetContacts


    private String readWhatsappContacts() {
        //read whatsapp contacts
        String success = "";
        ContentResolver cr = this.getContentResolver();
        //RowContacts for filter Account Types
        Cursor contactCursor = cr.query(
                ContactsContract.RawContacts.CONTENT_URI,
                new String[]{ContactsContract.RawContacts._ID,
                        ContactsContract.RawContacts.CONTACT_ID},
                ContactsContract.RawContacts.ACCOUNT_TYPE + "= ?",
                new String[]{"com.whatsapp"},
                "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");


//ArrayList for Store Whatsapp Contact
        if (contactCursor != null) {
            if (contactCursor.getCount() > 0) {
                if (contactCursor.moveToFirst()) {
                    do {
                        //whatsappContactId for get Number,Name,Id ect... from  ContactsContract.CommonDataKinds.Phone
                        String whatsappContactId = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.RawContacts._ID));

                        if (whatsappContactId != null) {
                            //Get Data from ContactsContract.CommonDataKinds.Phone of Specific CONTACT_ID

                            Cursor whatsAppContactCursor = cr.query(
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.PHOTO_URI},
                                    ContactsContract.Data.RAW_CONTACT_ID + " = ?",
                                    new String[]{whatsappContactId}, null);

                            if (whatsAppContactCursor != null && whatsAppContactCursor.getCount() > 0) {
                                PhoneContact phn_WA = new PhoneContact();
                                whatsAppContactCursor.moveToFirst();
                                String id = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                                String name = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                String number = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                String image_uri = whatsAppContactCursor.getString(whatsAppContactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                                Bitmap bp = null;
                                byte[] byteArray=null;
                                if (image_uri != null) {
                                    try {
                                        bp = MediaStore.Images.Media
                                                .getBitmap(mContext.getContentResolver(),
                                                        Uri.parse(image_uri));
                                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                        bp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                        byteArray = stream.toByteArray();
                                    } catch (FileNotFoundException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    } catch (IOException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                                phn_WA.setPhone_ID(id);
                                phn_WA.setPhone_Name(name);
                                phn_WA.setPhone_Mobile(number.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());
                                whatsAppContactCursor.close();

                                //Add Number to ArrayList
                                myWAContacts_id.add(id);
                                phn_WA.setPhoto_id(bp);
                                myWAContacts.add(phn_WA);

                                Log.d("TAG ", " WhatsApp contact id  :  " + id);
                                Log.d("TAG ", " WhatsApp contact name :  " + name);
                                Log.d("TAG", " WhatsApp contact number :  " + number);
                            }
                        }
                    } while (contactCursor.moveToNext());
                    contactCursor.close();

                }
            }
        }
        success = "success";

        Log.d("TAG ", " WhatsApp contact size  :  " + myWAContacts.size() + "WhatsApp id size" + myWAContacts_id.size());

        return success;
    }

    private void mInItObjects() {
        mBun = getIntent().getExtras();
        mFollower = mBun.getParcelableArrayList(StaticUtils.IPC_FOLLOWERS);
        mySyteId = mBun.getString(StaticUtils.IPC_SYTE_ID);
        mySyte = mBun.getParcelable(StaticUtils.IPC_SYTE);
        Authored_numbers = mBun.getStringArrayList("authored");
        mYasPasPref = YasPasPreferences.GET_INSTANCE(WhatsappActivity.this);
        layoutManager = new LinearLayoutManager(WhatsappActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mContext = this;
        myWAContacts_id = new ArrayList<>();
        myWAContacts_photo = new ArrayList<>();
        myWAContacts = new ArrayList<>();
        mInvited_numbers = new ArrayList<>();
        mPrgDia = new ProgressDialog(WhatsappActivity.this);
        mPrgDia.setMessage(getString(R.string.prg_bar_wait));
        mPrgDia.setCancelable(false);
    }// END mInItObjects

    @Override
    protected void onPause() {
        super.onPause();
        if (mFollower != null) {
            mFollower.clear();
        }
        if (myWAContacts != null) {
            myWAContacts.clear();
        }
        if (myWAContacts_id != null) {
            myWAContacts_id.clear();
        }
        if (myWAContacts_photo != null) {
            myWAContacts_photo.clear();
        }
        if (Authored_numbers != null) {
            Authored_numbers.clear();
        }
        if (mInvited_numbers != null) {
            mInvited_numbers.clear();
        }
        if (adapterWA != null) {
            adapterWA = null;
            mRvWAContacts.setAdapter(null);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapterWA = null;
        mRvWAContacts.setAdapter(null);
        mRvWAContacts = null;

    }

    // TextWatcher for Serach Box
    private final TextWatcher mSearchBoxTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Log.e("beforeTextChanged","beforeTextChanged");

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Log.e("onTextChanged","onTextChanged");
            adapterWA.getFilter().filter(s);//filter for search functioanlity
        }

        public void afterTextChanged(Editable s) {
            //Log.e("afterTextChanged","afterTextChanged");


        }

    };

    private void mInItWidgets() {
        mRelLayWABack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayWABack.setOnClickListener(this);
        mRelaysearch = (RelativeLayout) findViewById(R.id.xRelLayWhatsappSearch);
        mRelaysearch.setOnClickListener(this);
        mLinLaySearchBox = (LinearLayout) findViewById(R.id.xLinLaySearchBox);
        mEtSearchContact = (EditText) findViewById(R.id.xEtSearchContact);
        mEtSearchContact.addTextChangedListener(mSearchBoxTextWatcher);
        msearch_close = (ImageView) findViewById(R.id.search_close);
        msearch_close.setOnClickListener(this);
        mPrgDia = new ProgressDialog(this);
        mRvWAContacts = (RecyclerView) findViewById(R.id.xRvWAcontacts);
        mRvWAContacts.setLayoutManager(layoutManager);
        mTvWACenterLbl = (TextView) findViewById(R.id.xTvWACenterLbl);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }
            case R.id.xRelLayWhatsappSearch: {
                mRelLayWABack.setVisibility(View.GONE);
                mRelaysearch.setVisibility(View.GONE);
                mLinLaySearchBox.setVisibility(View.VISIBLE);

            }
            break;
            case R.id.search_close:
                mLinLaySearchBox.setVisibility(View.GONE);
                mRelLayWABack.setVisibility(View.VISIBLE);
                mRelaysearch.setVisibility(View.VISIBLE);
                break;

        }
    }
}





