package com.example.life.chatActivities;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.life.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageAdaptor extends RecyclerView.Adapter<MessageAdaptor.MessageViewHolder> {

    private List<Messages>userMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    public MessageAdaptor(List<Messages>userMessageList){
        this.userMessageList = userMessageList;
    }
    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView senderMessageText, receiverMessageText;
        public CircleImageView receiverProfileImage;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            senderMessageText = itemView.findViewById(R.id.senderMessageText);
            receiverMessageText = itemView.findViewById(R.id.receiverMessageText);
            receiverProfileImage = itemView.findViewById(R.id.messageProfileImage);
        }
    }


    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.custom_message_layout,viewGroup,false);

        mAuth  = FirebaseAuth.getInstance();


        return new MessageViewHolder(view);
    }





    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder holder, int position) {

        String messageSenderID = mAuth.getCurrentUser().getUid();
        Messages messages = userMessageList.get(position);

        String fromUserID = messages.getFrom();
        String fromMessageType = messages.getType();

        usersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserID);

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("profileimage")){
                    String receiverImage= dataSnapshot.child("profileimage").getValue().toString();
                    Picasso.get().load(receiverImage).placeholder(R.drawable.ic_launcher_background).into(holder.receiverProfileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if(fromMessageType.equals("text")){
            holder.receiverMessageText.setVisibility(View.INVISIBLE);
            holder.receiverProfileImage.setVisibility(View.INVISIBLE);
            holder.senderMessageText.setVisibility(View.INVISIBLE);

        }
        if(fromUserID.equals(messageSenderID)){
            holder.senderMessageText.setVisibility(View.VISIBLE);
            holder.senderMessageText.setBackgroundResource(R.drawable.sender_messages_layout);
            holder.senderMessageText.setTextColor(Color.WHITE);
            holder.senderMessageText.setText(messages.getMessage());
        }else {
            holder.receiverMessageText.setVisibility(View.VISIBLE);
            holder.receiverProfileImage.setVisibility(View.VISIBLE);

            holder.receiverMessageText.setBackgroundResource(R.drawable.receiver_messages_layout);
            holder.receiverMessageText.setTextColor(Color.BLACK);
            holder.receiverMessageText.setText(messages.getMessage());
        }



    }




    @Override
    public int getItemCount() {
        return userMessageList.size();
    }


}

