package com.syte.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.syte.R;
import com.syte.models.ChatMessage;
import com.syte.utils.StaticUtils;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by khalid.p on 16-03-2016.
 */
public class AdapterSponsorChatMessage extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        private ArrayList<ChatMessage> chatMessages;
        private ArrayList<String> chatMessagesIds;
        private Firebase firebaseChatMessage,firebaseChat;
        public AdapterSponsorChatMessage(ArrayList<ChatMessage> paramChatMessages,ArrayList<String> paramChatMessagesIds,Firebase paramFirebaseChatMessage,Firebase paramFirebaseChat)
            {
                this.chatMessages=paramChatMessages;
                this.chatMessagesIds=paramChatMessagesIds;
                this.firebaseChatMessage=paramFirebaseChatMessage;
                this.firebaseChat=paramFirebaseChat;
            }
        private final int SENDER_USER = 0, SENDER_SPONSOR = 1;
        @Override
        public int getItemViewType(int position)
            {
                ChatMessage chatMessage = chatMessages.get(position);
                if (chatMessage.getcSenderType()==SENDER_SPONSOR)
                {
                    return SENDER_USER;
                }
                else
                {
                    return SENDER_SPONSOR;
                }
            }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
            {
                RecyclerView.ViewHolder viewHolder;
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                switch (viewType)
                    {
                        case SENDER_USER:
                            View v1 = inflater.inflate(R.layout.vw_holder_user_own_chat_message, parent, false);
                            viewHolder = new ViewHolderUserOwnChatMessage(v1);
                            break;
                        case SENDER_SPONSOR:
                            View v2 = inflater.inflate(R.layout.vw_holder_other_user_chat_message, parent, false);
                            viewHolder = new ViewHolderOtherUserChatMessage(v2);
                            break;
                        default:
                            View v3 = inflater.inflate(R.layout.vw_holder_user_own_chat_message, parent, false);
                            viewHolder = new ViewHolderUserOwnChatMessage(v3);
                            break;
                    }
                return viewHolder;
            }
        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position)
            {
                switch (viewHolder.getItemViewType())
                    {
                        case SENDER_USER:
                            ViewHolderUserOwnChatMessage vh1 = (ViewHolderUserOwnChatMessage) viewHolder;
                            configureOwnMessage(vh1, position);
                            break;
                        case SENDER_SPONSOR:
                            ViewHolderOtherUserChatMessage vh2 = (ViewHolderOtherUserChatMessage) viewHolder;
                            configureOtherUserMessage(vh2, position);
                            break;
                        default:
                            ViewHolderUserOwnChatMessage vh = (ViewHolderUserOwnChatMessage) viewHolder;
                            configureOwnMessage(vh, position);
                            break;
                    }
            }
        private void configureOwnMessage(ViewHolderUserOwnChatMessage paramVh, int paramPos)
            {
                ChatMessage chatMessage = chatMessages.get(paramPos);
                paramVh.getmTvMessage().setText(chatMessage.getcMessage());
                int readImageId = chatMessage.getcIsRead()==false ? R.drawable.ic_chat_un_read : R.drawable.ic_chat_read;
                paramVh.getmIvReadStatus().setImageResource(readImageId);
                paramVh.getmTvTime().setText(StaticUtils.CONVERT_BULLETIN_DATE_TIME((long)chatMessage.getcDateTime()));
            }
        private void configureOtherUserMessage(ViewHolderOtherUserChatMessage paramVh, int paramPos)
            {
                ChatMessage chatMessage = chatMessages.get(paramPos);
                paramVh.getmTvMessage().setText(chatMessage.getcMessage());
                paramVh.getmTvTime().setText(StaticUtils.CONVERT_BULLETIN_DATE_TIME((long) chatMessage.getcDateTime()));
                firebaseChatMessage.child(chatMessagesIds.get(paramPos)).child("cIsRead").setValue(true);
                updateOwnUnreadCounter();
            }
        @Override
        public int getItemCount()
            {
                return chatMessages.size();
            }

        private void updateOwnUnreadCounter()
        {
            firebaseChatMessage.orderByChild("cSenderType").startAt(0).endAt(0).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    int unReadCounter = 0;
                    Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                    while (iterator.hasNext()) {
                        DataSnapshot dataSnapshot1 = (DataSnapshot) iterator.next();
                        ChatMessage chatMsg = dataSnapshot1.getValue(ChatMessage.class);
                        if (!chatMsg.getcIsRead()) {
                            unReadCounter++;
                        }
                    }
                    firebaseChat.child("unReadCount").setValue(unReadCounter);

                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }
