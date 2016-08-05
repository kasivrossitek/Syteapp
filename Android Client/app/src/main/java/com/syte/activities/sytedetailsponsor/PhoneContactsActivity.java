package com.syte.activities.sytedetailsponsor;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.adapters.AdapterPhoneContacts;
import com.syte.models.AuthDb;
import com.syte.models.Followers;
import com.syte.models.Listcontacts;
import com.syte.models.PhoneContact;
import com.syte.models.Syte;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by kasi.v on 24-05-2016.
 */
public class PhoneContactsActivity extends AppCompatActivity implements View.OnClickListener {
    private RelativeLayout mRelLayBack, mRelaysearch;
    private ImageView msearch_close;
    private LinearLayout mLinLaySearchBox;
    private EditText mEtSearchContact;
    private RecyclerView mRvContacts;
    private LinearLayoutManager layoutManager;
    private Context mContext;
    private ProgressDialog mPrgDia;
    private TextView mTvCenterLbl;
    private AdapterPhoneContacts adapterContacts;
    private ArrayList<Listcontacts> contact_id;
    private Bundle mBun;
    private String mySyteId;
    private Syte mySyte;
    private YasPasPreferences mYasPasPref;
    private ArrayList<Followers> mFollower;
    private ArrayList<String> Authored_numbers, mInvited_numbers;
    ArrayList<PhoneContact> contact_numbers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_contacts);
        mInItObjects();
        mInItWidgets();
    }// END onCreate()


    @Override
    protected void onPause() {
        super.onPause();
        if (contact_id != null) {
            contact_id.clear();
        }
        if (mFollower != null) {
            mFollower.clear();
        }
        if (mInvited_numbers != null) {
            mInvited_numbers.clear();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        adapterContacts = null;
        mRvContacts.setAdapter(null);
        mRvContacts = null;

    }

    @Override
    protected void onStart() {
        super.onStart();
        getAuthorisedNumbers();
        mGetContacts();
    }

    private void getAuthorisedNumbers() {
        Firebase mFirebaseAuthDbUrl = new Firebase(StaticUtils.AUTH_DB_URL);
        mFirebaseAuthDbUrl.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (Authored_numbers.size() != 0) {
                    Authored_numbers.removeAll(Authored_numbers);
                }
                if (dataSnapshot.getValue() != null) {
                    Log.d("autho", dataSnapshot.getKey());
                    Iterator<DataSnapshot> iteratorFollowers = dataSnapshot.getChildren().iterator();
                    while (iteratorFollowers.hasNext()) {
                        DataSnapshot dataSnapshotFollower = (DataSnapshot) iteratorFollowers.next();
                        AuthDb data = dataSnapshotFollower.getValue(AuthDb.class);
                        Log.d("autho data", data.getRegisteredNum());
                        Authored_numbers.add(data.getRegisteredNum());
                    }
                }

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("firebaseError", firebaseError.getMessage());

            }
        });
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

    private void mInItObjects() {
        mBun = getIntent().getExtras();
        mFollower = mBun.getParcelableArrayList(StaticUtils.IPC_FOLLOWERS);
        mySyteId = mBun.getString(StaticUtils.IPC_SYTE_ID);
        mySyte = getIntent().getExtras().getParcelable(StaticUtils.IPC_SYTE);
        mYasPasPref = YasPasPreferences.GET_INSTANCE(PhoneContactsActivity.this);
        mContext = this;
        contact_id = new ArrayList<>();
        Authored_numbers = new ArrayList<>();
        mInvited_numbers = new ArrayList<>();
        contact_numbers = new ArrayList<>();


    }// END mInItObjects


    private void mInItWidgets() {
        layoutManager = new LinearLayoutManager(PhoneContactsActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRelLayBack = (RelativeLayout) findViewById(R.id.xRelLayBack);
        mRelLayBack.setOnClickListener(this);
        mRelaysearch = (RelativeLayout) findViewById(R.id.xRelLayphnSearch);
        mRelaysearch.setOnClickListener(this);
        mLinLaySearchBox = (LinearLayout) findViewById(R.id.xLinLaySearchBox);
        mEtSearchContact = (EditText) findViewById(R.id.xEtSearchContact);
        mEtSearchContact.addTextChangedListener(mSearchBoxTextWatcher);
        msearch_close = (ImageView) findViewById(R.id.search_close);
        msearch_close.setOnClickListener(this);
        mRvContacts = (RecyclerView) findViewById(R.id.xRvcontacts);
        mRvContacts.setLayoutManager(layoutManager);
        mPrgDia = new ProgressDialog(this);
        mTvCenterLbl = (TextView) findViewById(R.id.xTvCenterLbl);
    }

    public String readContacts() {
        //Read phone contacts
        String str = "";
        ContentResolver cr = this.getContentResolver();

        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");
        contact_id.clear();

        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {

                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                String image_uri = cur
                        .getString(cur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                Bitmap bp = null;

                if (image_uri != null) {
                    try {
                        bp = MediaStore.Images.Media
                                .getBitmap(mContext.getContentResolver(),
                                        Uri.parse(image_uri));
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);
                    Log.d("names ", name + ", IDs : " + id);
                    Listcontacts listcontacts = new Listcontacts();
                    listcontacts.setContact_id(id);
                    listcontacts.setName(name);
                    listcontacts.setPhoto_id(bp);
                    contact_id.add(listcontacts);
                }
            }
            cur.close();

            Log.d("idsize" + contact_id.size(), "");
            str = read_phoneNum();


        }
        return str;
    }

    // TextWatcher for Serach Box
    private final TextWatcher mSearchBoxTextWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //Log.e("beforeTextChanged","beforeTextChanged");

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //Log.e("onTextChanged","onTextChanged");
            adapterContacts.getFilter().filter(s);
        }

        public void afterTextChanged(Editable s) {
            //Log.e("afterTextChanged","afterTextChanged");


        }

    };

    private void mGetContacts() {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                mInvites();
                String str = readContacts();//get contacts from phone contacts
                // String str = getcontacts_from_gsondata();//13-7-16 get contacts from cache
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
                if (result.equalsIgnoreCase("success")) {
                    adapterContacts = new AdapterPhoneContacts(contact_id, contact_numbers, mFollower, Authored_numbers, mySyte, mySyteId, mYasPasPref, mInvited_numbers, ((int) (getResources().getDimension(R.dimen.cloudinary_list_image_sz) / getResources().getDisplayMetrics().density)), getApplicationContext());
                    mRvContacts.setAdapter(adapterContacts);
                } else {

                    Toast.makeText(PhoneContactsActivity.this, "No Contacts found", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    } // END mGetContacts


    public String read_phoneNum() {

        String success = "";

        ContentResolver cr = this.getContentResolver();
        for (Listcontacts ids : contact_id) {

            Log.d("idsss", ids.getContact_id());
            // get the phone number
            Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{ids.getContact_id()}, "upper(" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");

            while (pCur.moveToNext()) {
                PhoneContact phncon = new PhoneContact();
                int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                String phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                switch (phoneType) {
                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                        Log.e(": TYPE_MOBILE", " " + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());

                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                        Log.e(": TYPE_HOME", " " + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());

                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                        Log.e(": TYPE_WORK", " " + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE:
                        Log.e(": TYPE_WORK_MOBILE", " " + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());
                        break;
                    case ContactsContract.CommonDataKinds.Phone.TYPE_OTHER:
                        Log.e(": TYPE_OTHER", "" + phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());
                        break;
                    default:
                        break;
                }

                phncon.setPhone_Mobile(phone.replaceAll("\\s+", "").replaceFirst("((\\+91)|0|(\\+1)|1?)", "").trim());
                phncon.setPhone_ID(ids.getContact_id());
                System.out.println("phone" + phone);
                contact_numbers.add(phncon);
            }
            pCur.close();
            success = "success";
        }


        return success;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xRelLayBack: {
                finish();
                break;
            }

            case R.id.xRelLayphnSearch: {
                mRelLayBack.setVisibility(View.GONE);
                mRelaysearch.setVisibility(View.GONE);
                mLinLaySearchBox.setVisibility(View.VISIBLE);
            }
            break;
            case R.id.search_close:
                mEtSearchContact.setText("");
                mLinLaySearchBox.setVisibility(View.GONE);
                mRelLayBack.setVisibility(View.VISIBLE);
                mRelaysearch.setVisibility(View.VISIBLE);
                break;
            default:
                break;

        }
    }


}
