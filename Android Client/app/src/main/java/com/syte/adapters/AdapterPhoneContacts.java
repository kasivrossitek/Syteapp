package com.syte.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;


import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
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
 * Created by kasi.v on 24-05-2016.
 */
public class AdapterPhoneContacts extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context mContext;
    private Syte mysyte;
    private String mysyteid;
    private YasPasPreferences myYasPasPref;
    private ArrayList<PhoneContact> contactnumber;
    private ArrayList<Followers> followerses;

    private ArrayList<String> registered_numbers, invited_numbers;

    private ArrayList<Listcontacts> contactId, contactList_Filtered;


    ArrayList<PhoneContact> followlist = new ArrayList<>();
    ArrayList<PhoneContact> regis_cont = new ArrayList<>();


    public AdapterPhoneContacts(ArrayList<Listcontacts> contact_id, ArrayList<PhoneContact> contact_numbers, ArrayList<Followers> mFollower, ArrayList<String> authored_numbers, Syte mySyte, String mySyteId, YasPasPreferences mYasPasPref, ArrayList<String> mInvited_numbers, int i, Context applicationContext) {

        this.contactId = contact_id;
        this.contactnumber = contact_numbers;
        this.mContext = applicationContext;
        this.followerses = mFollower;
        this.registered_numbers = authored_numbers;
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
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        recyclerView.invalidate();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final ViewHolderContactlist viewHolder = (ViewHolderContactlist) holder;
        Log.d("adapter name", contactId.get(position).getName());

        contacts();

        Listcontacts id = contactId.get(position);
        if (regis_cont != null) {
            boolean isidregs = checkreg_list(id.getContact_id());
            if (isidregs) {
                if (followlist != null) {
                    boolean isidfollows = checkfollow_list(id.getContact_id());
                    if (isidfollows) {
                        displayfollowContact(viewHolder, position, id.getContact_id());
                    } else {
                        displayunfollowContact(viewHolder, position, id.getContact_id());
                    }
                } else {
                    displayunfollowContact(viewHolder, position, id.getContact_id());
                }
            } else {
                displayInviteContact(viewHolder, position, id.getContact_id());
            }
        } else {
            displayInviteContact(viewHolder, position, id.getContact_id());
        }
        viewHolder.getmUnfollowfnd().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("unfollowed", "notification");
                Listcontacts cont_id = contactId.get(position);
                if (cont_id != null) {
                    ArrayList<String> numbers = new ArrayList<String>();
                    for (PhoneContact getphn : contactnumber) {
                        if (cont_id.getContact_id().equalsIgnoreCase(getphn.getPhone_ID())) {
                            numbers.add(getphn.getPhone_Mobile());
                        }
                    }
                    if (numbers != null) {
                        Log.d("unfollowed", numbers.get(0) + "size:" + numbers.size());

                        mSendFollowInviteNotification(numbers.get(0));
                        contactfollowInvited(viewHolder, position, cont_id.getContact_id());

                        Log.d("Follow request", "Follow Request Sent Successfully" + position);

                        //  Snackbar.make(v.getRootView(), "Follow Request Sent Successfully" + numbers.get(0), Snackbar.LENGTH_SHORT);

                    }
                }
            }
        });
        viewHolder.getmInvitefnd().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("invite", "syte not installed");
                Listcontacts cont_id = contactId.get(position);
                if (cont_id != null) {
                    ArrayList<String> numbers = new ArrayList<String>();
                    for (PhoneContact getphn : contactnumber) {
                        if (cont_id.getContact_id().equalsIgnoreCase(getphn.getPhone_ID())) {
                            numbers.add(getphn.getPhone_Mobile());
                        }
                    }
                    if (numbers != null) {
                        Log.d("invite", numbers.get(0) + "size:" + numbers.size());
                        sendMessage(numbers.get(0));
                    }
                }


            }
        });

    }

    private int getImageResource(ImageView iv) {
        return (Integer) iv.getTag();
    }


    //follow invite push notification
    private void mSendFollowInviteNotification(String num) {

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


    //updating to follow invite in firebase
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

    //send message to install app
    private void sendMessage(String num) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        String smsMsg = "Hi I am using Syte App. Create your own free Syte & start managing it. Download : https://play.google.com/store/apps/details?id=com.syte";
        sendIntent.putExtra("address", num);
        sendIntent.putExtra("sms_body", smsMsg);
        sendIntent.setType("vnd.android-dir/mms-sms");
        sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(sendIntent);

    }
    //flipping follow invited contact
    private void contactfollowInvited(ViewHolderContactlist viewHolders, int pos, String id) {
        viewHolders.getmTvName().setText(contactId.get(pos).getName());
        Bitmap img = contactId.get(pos).getPhoto_id();
        if (img != null) {
            //  ImageLoader.getInstance().displayImage(img.toString(),viewHolders.getmProfilePic(),mDspImgOptions);
            Bitmap bmp = getRoundedShape(img);
            viewHolders.getmProfilePic().setImageBitmap(bmp);
        } else {
            viewHolders.getmProfilePic().setImageResource(R.drawable.img_user_thumbnail_image_list);
        }
        // viewHolders.getmInvitefnd().setImageResource(R.drawable.ic_syte_installed);
        viewHolders.getMinvited().setVisibility(View.VISIBLE);
        viewHolders.getmInvitefnd().setVisibility(View.GONE);
        viewHolders.getmUnfollowfnd().setVisibility(View.GONE);
        viewHolders.getMfollowfnd().setVisibility(View.GONE);
        notifyDataSetChanged();

    }

    //to display followed contacts

    private void displayfollowContact(ViewHolderContactlist viewHolders, int pos, String id) {
        viewHolders.getmTvName().setText(contactId.get(pos).getName());
        Bitmap img = contactId.get(pos).getPhoto_id();
        if (img != null) {
            //  ImageLoader.getInstance().displayImage(img.toString(),viewHolders.getmProfilePic(),mDspImgOptions);
            Bitmap bmp = getRoundedShape(img);
            viewHolders.getmProfilePic().setImageBitmap(bmp);
        } else {
            viewHolders.getmProfilePic().setImageResource(R.drawable.img_user_thumbnail_image_list);
        }
        // viewHolders.getmInvitefnd().setImageResource(R.drawable.ic_syte_installed);
        viewHolders.getmInvitefnd().setVisibility(View.GONE);
        viewHolders.getmUnfollowfnd().setVisibility(View.GONE);
        viewHolders.getMfollowfnd().setVisibility(View.VISIBLE);
    }

    //to display unfollowed / follow invited contacts
    private void displayunfollowContact(ViewHolderContactlist viewHolders, int pos, String id) {

        if (invited_numbers.size() != 0) {
            for (String invitednum : invited_numbers) {
                ArrayList<String> numbers = new ArrayList<String>();
                Listcontacts cont_id = contactId.get(pos);
                if (cont_id != null) {

                    for (PhoneContact getphn : contactnumber) {
                        if (cont_id.getContact_id().equalsIgnoreCase(getphn.getPhone_ID())) {
                            numbers.add(getphn.getPhone_Mobile());
                        }
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
        } else {

            displayfollowInvited(viewHolders, pos, id, false);

        }
    }//end unfollowed/followinvited

    private void displayfollowInvited(ViewHolderContactlist viewHolders, int pos, String id, boolean isInvited) {

        viewHolders.getmTvName().setText(contactId.get(pos).getName());
        Bitmap img = contactId.get(pos).getPhoto_id();
        if (img != null) {

            // ImageLoader.getInstance().displayImage(img.toString(), viewHolders.getmProfilePic(), mDspImgOptions);
            Bitmap bmp = getRoundedShape(img);
            viewHolders.getmProfilePic().setImageBitmap(bmp);
        } else {
            viewHolders.getmProfilePic().setImageResource(R.drawable.img_user_thumbnail_image_list);
        }
        if (isInvited) {
            // viewHolders.getmInvitefnd().setImageResource(R.drawable.ic_syte_not_installed);
            viewHolders.getMinvited().setVisibility(View.VISIBLE);
            viewHolders.getmInvitefnd().setVisibility(View.GONE);
            viewHolders.getmUnfollowfnd().setVisibility(View.GONE);
            viewHolders.getMfollowfnd().setVisibility(View.GONE);
        } else {
            viewHolders.getMinvited().setVisibility(View.GONE);
            viewHolders.getmInvitefnd().setVisibility(View.GONE);
            viewHolders.getmUnfollowfnd().setVisibility(View.VISIBLE);
            viewHolders.getMfollowfnd().setVisibility(View.GONE);
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

    private void displayInviteContact(ViewHolderContactlist viewHolders, int pos, String id) {
        viewHolders.getmTvName().setText(contactId.get(pos).getName());
        Bitmap img = contactId.get(pos).getPhoto_id();
        if (img != null) {
            Bitmap bmp = getRoundedShape(img);
            // ImageLoader.getInstance().displayImage(img.toString(), viewHolders.getmProfilePic(), mDspImgOptions);
            viewHolders.getmProfilePic().setImageBitmap(bmp);
        } else {
            viewHolders.getmProfilePic().setImageResource(R.drawable.img_user_thumbnail_image_list);
        }
        // viewHolders.getmInvitefnd().setImageResource(R.drawable.ic_send_invite);

        viewHolders.getMinvited().setVisibility(View.GONE);

        viewHolders.getmInvitefnd().setVisibility(View.VISIBLE);
        viewHolders.getmUnfollowfnd().setVisibility(View.GONE);
        viewHolders.getMfollowfnd().setVisibility(View.GONE);


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


    //filtering registered contacts and following
    private void contacts() {
        for (PhoneContact phc : contactnumber) {
            isregistered(phc);
        }
        if (regis_cont != null) {
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

        for (String number : registered_numbers) {
            if (num_lists.getPhone_Mobile() != null) {
                if (registered_numbers.contains(num_lists.getPhone_Mobile())) {
                    regis_cont.add(num_lists);
                }
            }

        }
    }


    @Override
    public int getItemCount() {
        return contactId.size();
    }


    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                contactId = (ArrayList<Listcontacts>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<Listcontacts> FilteredArrList = new ArrayList<Listcontacts>();

                if (contactList_Filtered == null) {
                    contactList_Filtered = new ArrayList<Listcontacts>(contactId); // saves the original data in mOriginalValues
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
                        String data = contactList_Filtered.get(i).getName();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new Listcontacts(contactList_Filtered.get(i).getName(), contactList_Filtered.get(i).getContact_id(), contactList_Filtered.get(i).getPhoto_id()));
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

    public class ViewHolderContactlist extends RecyclerView.ViewHolder {
        private ImageView mProfilePic, mInvitefnd, mUnfollowfnd, mfollowfnd, minvited;

        private TextView mTvName;

        public ImageView getMinvited() {
            return minvited;
        }

        public void setMinvited(ImageView minvited) {
            this.minvited = minvited;
        }
        public ViewHolderContactlist(View itemView) {
            super(itemView);
            mProfilePic = (ImageView) itemView.findViewById(R.id.xfriendsprofileimgid);
            mInvitefnd = (ImageView) itemView.findViewById(R.id.xfriendinviteimgid);
            mfollowfnd = (ImageView) itemView.findViewById(R.id.xfriendfollowimgid);
            mUnfollowfnd = (ImageView) itemView.findViewById(R.id.xfriendunfollowimgid);
            minvited = (ImageView) itemView.findViewById(R.id.xfriendivited);

            mTvName = (TextView) itemView.findViewById(R.id.xfriendsNameid);
        }

        public ImageView getmProfilePic() {
            return mProfilePic;
        }

        public void setmProfilePic(ImageView mProfilePic) {
            this.mProfilePic = mProfilePic;
        }

        public ImageView getmUnfollowfnd() {
            return mUnfollowfnd;
        }

        public void setmUnfollowfnd(ImageView mUnfollowfnd) {
            this.mUnfollowfnd = mUnfollowfnd;
        }

        public ImageView getMfollowfnd() {
            return mfollowfnd;
        }

        public void setMfollowfnd(ImageView mfollowfnd) {
            this.mfollowfnd = mfollowfnd;
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
