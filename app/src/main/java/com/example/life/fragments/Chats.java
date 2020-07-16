package com.example.life.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.solver.widgets.Snapshot;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.life.R;
import com.example.life.chatActivities.ChatActivity;
import com.example.life.chatActivities.Contacts;
import com.example.life.chatActivities.PrivateChatActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;


public class Chats extends Fragment {
    private View PrivateChatsView;
    private RecyclerView chatsList;

    private DatabaseReference ChatsRef, UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID="";

    public Chats(){

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        PrivateChatsView = inflater.inflate(R.layout.fragment_chats, container, false);


        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");


        chatsList = (RecyclerView) PrivateChatsView.findViewById(R.id.recyclerViewChat);
        chatsList.setLayoutManager(new LinearLayoutManager(getContext()));


        return PrivateChatsView;
    }


    @Override
    public void onStart()
    {
        super.onStart();


        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ChatsRef, Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts, ChatsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model)
                    {
                        final String usersIDs = getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                if (dataSnapshot.exists())
                                {
                                    if (dataSnapshot.hasChild("profileimage"))
                                    {
                                        retImage[0] = dataSnapshot.child("profileimage").getValue().toString();
                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
                                    }


                                    final String retStatus = dataSnapshot.child("status").getValue().toString();

                                    final String retName = dataSnapshot.child("username").getValue().toString();

                                    Log.e("Log","Username "+ retName);

                                    holder.userStatus.setText("Last Seen: " + "date "+ "\n " +" time");
                                    holder.userName.setText(retName);
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view)
                                        {
                                            Intent chatIntent = new Intent(getContext(), PrivateChatActivity.class);
                                            chatIntent.putExtra("visitUserId", usersIDs);
                                            chatIntent.putExtra("visitUserName", retName);
                                            chatIntent.putExtra("visitImage", retImage[0]);
                                            startActivity(chatIntent);
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
                    {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_items, viewGroup, false);
                        return new ChatsViewHolder(view);
                    }
                };

        chatsList.setAdapter(adapter);
        adapter.startListening();
    }




    public static class  ChatsViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus, userName;


        public ChatsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            profileImage = itemView.findViewById(R.id.listProfileImage );
            userStatus = itemView.findViewById(R.id.listStatus);
            userName = itemView.findViewById(R.id.listName);
        }
    }
}