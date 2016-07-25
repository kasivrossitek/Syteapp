package com.syte.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.syte.R;
import com.syte.models.Followers;
import com.syte.models.Listcontacts;
import com.syte.models.PhoneContact;
import com.syte.models.Syte;
import com.syte.models.YasPasPush;
import com.syte.utils.StaticUtils;
import com.syte.utils.YasPasPreferences;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Developer on 6/1/2016.
 */
public class AdapterWhatsapp extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {


    private Context mContext;
    private ArrayList<PhoneContact> contactnumber, contactList_Filtered;
    private ArrayList<Followers> followerses;
    private ArrayList<String> registered_numbers, invited_numbers;
    private ArrayList<String> contactId;
    private Syte mysyte;
    private String mysyteid;
    private YasPasPreferences myYasPasPref;
    ArrayList<PhoneContact> followlist = new ArrayList<>();
    ArrayList<PhoneContact> regis_cont = new ArrayList<>();

    public AdapterWhatsapp(ArrayList<String> myWAContacts_id, ArrayList<Followers> mFollower, ArrayList<String> authored_numbers, ArrayList<PhoneContact> myWAContacts, Syte mySyte, String mySyteId, YasPasPreferences mYasPasPref, ArrayList<String> mInvited_numbers, int dimen, Context applicationContext) {
        this.mContext = applicationContext;
        this.contactId = myWAContacts_id;
        this.followerses = mFollower;
        this.registered_numbers = authored_numbers;
        this.contactnumber = myWAContacts;
        this.mysyte = mySyte;
        this.mysyteid = mySyteId;
        this.myYasPasPref = mYasPasPref;
        this.invited_numbers = mInvited_numbers;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phonecontactsadapter, parent, false);
        return new ViewHolderContactlist(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ViewHolderContactlist viewHolder = (ViewHolderContactlist) holder;
        Log.d("adapter name", contactId.get(position));
        contacts();
        String id = contactId.get(position);
        if (regis_cont != null) {
            boolean isidregs = checkreg_list(id);
            if (isidregs) {
                if (followlist.size() != 0) {
                    boolean isidfollows = checkfollow_list(id);
                    if (isidfollows) {
                        displayfollowContact(viewHolder, position, id);
                    } else {
                        displayunfollowContact(viewHolder, position, id);
                    }
                } else {
                    displayunfollowContact(viewHolder, position, id);
                }
            } else {
                displayListContact(viewHolder, position, id);
            }
        } else {
            displayListContact(viewHolder, position, id);
        }

        viewHolder.getmInvitefnd().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("Whatsapp invite", "syte not installed");
                sendWhatsapp();

             /*   String cont_id = contactId.get(position);
                if (cont_id != null) {
                    ArrayList<String> numbers = new ArrayList<String>();
                    for (PhoneContact getphn : contactnumber) {
                        if (cont_id.equalsIgnoreCase(getphn.getPhone_ID())) {
                            numbers.add(getphn.getPhone_Mobile());
                        }
                    }
                    if (numbers != null) {
                        Log.d("invite", numbers.get(0) + "size:" + numbers.size());
                        sendWhatsapp();

                    }
                }*/

            }
        });
        viewHolder.getmUnFollowfnd().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Whatsapp unfollow", "syte not installed");
                String cont_id = contactId.get(position);
                if (cont_id != null) {
                    ArrayList<String> numbers = new ArrayList<String>();
                    for (PhoneContact getphn : contactnumber) {
                        if (cont_id.equalsIgnoreCase(getphn.getPhone_ID())) {
                            numbers.add(getphn.getPhone_Mobile());
                        }
                    }
                    if (numbers != null) {
                        Log.d("Whatsapp unfollow", numbers.get(0) + "size:" + numbers.size());
                        mSendInviteNotification(numbers.get(0));

                        Log.d("Whatsapp Follow request", "Follow Request Sent Successfully");
                        //   Snackbar.make(R.id.xRelLayMain, "Follow Request Sent Successfully" + numbers.get(0), Snackbar.LENGTH_SHORT);

                    }
                }
            }
        });
    }

    //flipping follow invited contact
    private void contactfollowInvited(ViewHolderContactlist viewHolders, int pos, String id) {
        viewHolders.getmTvName().setText(contactnumber.get(pos).getPhone_Name());
        Bitmap img = contactnumber.get(pos).getPhoto_id();
        if (img != null) {
            Bitmap bmp = getRoundedShape(img);
            viewHolders.getmProfilePic().setImageBitmap(bmp);
        } else {
            viewHolders.getmProfilePic().setImageResource(R.drawable.img_user_thumbnail_image_list);
        }
        viewHolders.getMinvited().setVisibility(View.VISIBLE);
        viewHolders.getmInvitefnd().setVisibility(View.GONE);
        viewHolders.getmFollowfnd().setVisibility(View.GONE);
        viewHolders.getmUnFollowfnd().setVisibility(View.GONE);
        notifyDataSetChanged();

    }

    //send whatsapp message
    public void sendWhatsapp() {
        //  Uri mUri = Uri.parse("smsto:" + num.trim());
        Intent mIntent = new Intent(Intent.ACTION_SEND);
        mIntent.setPackage("com.whatsapp");
        mIntent.setType("text/plain");
        mIntent.putExtra("chat", true);
        mIntent.putExtra(Intent.EXTRA_TEXT, "Hi I am using Syte App. Create your own free Syte & start managing it. Download : https://play.google.com/store/apps/details?id=com.syte");
        mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            mContext.startActivity(mIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(mContext, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
       /* String whatsappid = null;
        Cursor c = mContext.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.Contacts.Data.RAW_CONTACT_ID}, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                new String[]{id}, null);

        c.moveToFirst();
        Log.d("whatsapps id", id + "number" + num + c.getString(c.getColumnIndex(ContactsContract.Contacts.Data.RAW_CONTACT_ID)));
      //  whatsappid = c.getString(c.getColumnIndex(ContactsContract.Contacts.Data._ID));
        c.close();
        //if (whatsappid != null) {
            Intent i = new Intent(Intent.ACTION_SEND, Uri.parse("content://com.android.contacts/data/" + num + "@s.whatsapp.net"));
            i.setPackage("com.whatsapp");
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_TEXT, "Hai Good Morning");
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            try {
                mContext.startActivity(i);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(mContext, "There are no email applications installed.", Toast.LENGTH_SHORT).show();
            }*/
        //}
    }

    @Override
    public int getItemCount() {
        return contactnumber.size();
    }

    private void contacts() {
        for (PhoneContact phc : contactnumber) {
            isregistered(phc);
        }
        Log.d("regis_cont", "" + regis_cont.size());
        if (regis_cont.size() != 0) {
            isfollowsyte(regis_cont);
        }
    }

    private void isfollowsyte(ArrayList<PhoneContact> regis_cont) {
        if (followerses != null) {
            for (int i = 0; i < followerses.size(); i++) {
                Followers follow = followerses.get(i);
                for (PhoneContact reg : regis_cont) {
                    if (reg.getPhone_Mobile().equalsIgnoreCase(follow.getRegisteredNum())) {
                        Log.d("follow", "" + reg.getPhone_ID());
                        followlist.add(reg);
                    }
                }

            }
        }

    }

    private void isregistered(PhoneContact num_lists) {

        Log.d("heloo", num_lists.getPhone_Mobile());
        if (num_lists.getPhone_Mobile() != null) {
            for (String num : registered_numbers) {
                Log.d("num", num);
                if (num_lists.getPhone_Mobile().equalsIgnoreCase(num)) {
                    regis_cont.add(num_lists);
                    break;
                }
            }
        }


    }

    private void displayfollowContact(ViewHolderContactlist viewHolders, int pos, String id) {
        viewHolders.getmTvName().setText(contactnumber.get(pos).getPhone_Name());
        Bitmap img = contactnumber.get(pos).getPhoto_id();
        if (img != null) {
            Bitmap bmp = getRoundedShape(img);
            viewHolders.getmProfilePic().setImageBitmap(bmp);
        } else {
            viewHolders.getmProfilePic().setImageResource(R.drawable.img_user_thumbnail_image_list);
        }
        viewHolders.getMinvited().setVisibility(View.GONE);
        viewHolders.getmInvitefnd().setVisibility(View.GONE);
        viewHolders.getmFollowfnd().setVisibility(View.VISIBLE);
        viewHolders.getmUnFollowfnd().setVisibility(View.GONE);


    }

    private void displayunfollowContact(ViewHolderContactlist viewHolders, int pos, String id) {
        if (invited_numbers.size() != 0) {
            for (String invitednum : invited_numbers) {
                String cont_id = contactId.get(pos);
                if (cont_id != null) {
                    ArrayList<String> numbers = new ArrayList<String>();
                    for (PhoneContact getphn : contactnumber) {
                        if (cont_id.equalsIgnoreCase(getphn.getPhone_ID())) {
                            numbers.add(getphn.getPhone_Mobile());
                        }
                    }

                    if (numbers.size() != 0) {
                        if (numbers.contains(invitednum)) {
                            displayfollowInvited(viewHolders, pos, id, true);
                            break;
                        } else {
                            displayfollowInvited(viewHolders, pos, id, false);
                        }
                    }
                }
            }
        } else {
            displayfollowInvited(viewHolders, pos, id, false);

        }

    }

    private void displayfollowInvited(ViewHolderContactlist viewHolders, int pos, String id, boolean isInvited) {

        viewHolders.getmTvName().setText(contactnumber.get(pos).getPhone_Name());
        Bitmap img = contactnumber.get(pos).getPhoto_id();
        if (img != null) {
            Bitmap bmp = getRoundedShape(img);
            viewHolders.getmProfilePic().setImageBitmap(bmp);
        } else {
            viewHolders.getmProfilePic().setImageResource(R.drawable.img_user_thumbnail_image_list);
        }
        if (isInvited) {
            viewHolders.getMinvited().setVisibility(View.VISIBLE);
            viewHolders.getmInvitefnd().setVisibility(View.GONE);
            viewHolders.getmFollowfnd().setVisibility(View.GONE);
            viewHolders.getmUnFollowfnd().setVisibility(View.GONE);
        } else {
            viewHolders.getMinvited().setVisibility(View.GONE);
            viewHolders.getmInvitefnd().setVisibility(View.GONE);
            viewHolders.getmFollowfnd().setVisibility(View.GONE);
            viewHolders.getmUnFollowfnd().setVisibility(View.VISIBLE);
        }
    }

    private boolean checkfollow_list(String ids) {
        boolean isfollow = false;
        for (PhoneContact phn : followlist) {
            if (phn.getPhone_ID().equalsIgnoreCase(ids)) {
                isfollow = true;
            }
        }

        return isfollow;
    }

    private void displayListContact(ViewHolderContactlist viewHolders, int pos, String id) {
        viewHolders.getmTvName().setText(contactnumber.get(pos).getPhone_Name());
        Bitmap img = contactnumber.get(pos).getPhoto_id();
        if (img != null) {
            Bitmap bmp = getRoundedShape(img);
            viewHolders.getmProfilePic().setImageBitmap(bmp);
        } else {
            viewHolders.getmProfilePic().setImageResource(R.drawable.img_user_thumbnail_image_list);
        }

        viewHolders.getmInvitefnd().setVisibility(View.VISIBLE);
        viewHolders.getmFollowfnd().setVisibility(View.GONE);
        viewHolders.getmUnFollowfnd().setVisibility(View.GONE);
        viewHolders.getMinvited().setVisibility(View.GONE);

    }

    private void mSendInviteNotification(String num) {
        updateinvite(num);
        Firebase firebaseYasPasPushNotification = new Firebase(StaticUtils.YASPAS_PUSH_NOTIFICATION_URL);
        YasPasPush yasPasPush = new YasPasPush();
        yasPasPush.setSyteId(mysyteid);
        yasPasPush.setSyteName(mysyte.getName());
        yasPasPush.setSyteOwner(mysyte.getOwner());
        yasPasPush.setRegisteredNum(num);
        yasPasPush.setUserName(myYasPasPref.sGetUserName());
        yasPasPush.setUserProfilePic(myYasPasPref.sGetUserProfilePic());
        yasPasPush.setDateTime(ServerValue.TIMESTAMP);
        yasPasPush.setPushType(StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_INVITE);
        firebaseYasPasPushNotification.push().setValue(yasPasPush, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                //  progressDialog.dismiss();
            }
        });
    }

    private void updateinvite(String num) {
        Firebase firebaseUpdateInviteStatus = new Firebase(StaticUtils.YASPAS_URL).child(mysyteid).child("invitetofollow").child(num);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("syteId", mysyteid);
        hashMap.put("syteName", mysyte.getName());
        hashMap.put("syteowner", mysyte.getOwner());
        hashMap.put("follow", "invited");
        hashMap.put("bulletinDateTime", ServerValue.TIMESTAMP);
        hashMap.put("pushType", StaticUtils.HOME_STARTING_FRAG_NOTIFICATION_SYTE_FOLLOW_INVITE);
        hashMap.put("registeredNum", num);
        firebaseUpdateInviteStatus.push().setValue(hashMap, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                //  progressDialog.dismiss();
            }
        });
    }

    public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
        // TODO Auto-generated method stub
        int targetWidth = 40;
        int targetHeight = 40;
        Bitmap targetBitmap = Bitmap.createBitmap(targetWidth,
                targetHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(targetBitmap);
        Path path = new Path();
        path.addCircle(((float) targetWidth - 1) / 2,
                ((float) targetHeight - 1) / 2,
                (Math.min(((float) targetWidth),
                        ((float) targetHeight)) / 2),
                Path.Direction.CCW);

        canvas.clipPath(path);
        Bitmap sourceBitmap = scaleBitmapImage;
        canvas.drawBitmap(sourceBitmap,
                new Rect(0, 0, sourceBitmap.getWidth(),
                        sourceBitmap.getHeight()),
                new Rect(0, 0, targetWidth,
                        targetHeight), null);
        return targetBitmap;
    }


    private boolean checkreg_list(String ids) {
        boolean regid = false;
        for (PhoneContact regphn : regis_cont) {
            if (regphn.getPhone_ID().equalsIgnoreCase(ids)) {
                regid = true;
                break;
            }

        }
        return regid;
    }

    @Override
    public Filter getFilter() {
        {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {

                    contactnumber = (ArrayList<PhoneContact>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<PhoneContact> FilteredArrList = new ArrayList<PhoneContact>();

                    if (contactList_Filtered == null) {
                        contactList_Filtered = new ArrayList<PhoneContact>(contactnumber); // saves the original data in mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = contactList_Filtered.size();
                        results.values = contactList_Filtered;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < contactList_Filtered.size(); i++) {
                            String data = contactList_Filtered.get(i).getPhone_Name();
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(new PhoneContact(contactList_Filtered.get(i).getPhone_Name(), contactList_Filtered.get(i).getPhone_ID(), contactList_Filtered.get(i).getPhoto_id(), contactList_Filtered.get(i).getPhone_Mobile()));
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;


                }
            };
            return filter;
        }
    }


    public class ViewHolderContactlist extends RecyclerView.ViewHolder {
        private ImageView mProfilePic, mInvitefnd, mFollowfnd, mUnFollowfnd, minvited;

        private TextView mTvName;

        public ViewHolderContactlist(View itemView) {
            super(itemView);
            mProfilePic = (ImageView) itemView.findViewById(R.id.xfriendsprofileimgid);
            mInvitefnd = (ImageView) itemView.findViewById(R.id.xfriendinviteimgid);
            mFollowfnd = (ImageView) itemView.findViewById(R.id.xfriendfollowimgid);
            mUnFollowfnd = (ImageView) itemView.findViewById(R.id.xfriendunfollowimgid);
            minvited = (ImageView) itemView.findViewById(R.id.xfriendivited);
            mTvName = (TextView) itemView.findViewById(R.id.xfriendsNameid);
        }

        public ImageView getMinvited() {
            return minvited;
        }

        public void setMinvited(ImageView minvited) {
            this.minvited = minvited;
        }

        public ImageView getmProfilePic() {
            return mProfilePic;
        }

        public ImageView getmFollowfnd() {
            return mFollowfnd;
        }

        public void setmFollowfnd(ImageView mFollowfnd) {
            this.mFollowfnd = mFollowfnd;
        }

        public ImageView getmUnFollowfnd() {
            return mUnFollowfnd;
        }

        public void setmUnFollowfnd(ImageView mUnFollowfnd) {
            this.mUnFollowfnd = mUnFollowfnd;
        }

        public void setmProfilePic(ImageView mProfilePic) {
            this.mProfilePic = mProfilePic;
        }

        public ImageView getmInvitefnd() {
            return mInvitefnd;
        }

        public void setmInvitefnd(ImageView mInvitefnd) {
            this.mInvitefnd = mInvitefnd;
        }

        public TextView getmTvName() {
            return mTvName;
        }

        public void setmTvName(TextView mTvName) {
            this.mTvName = mTvName;
        }
    }
}
