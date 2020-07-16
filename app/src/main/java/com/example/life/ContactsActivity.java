package com.example.life;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaRouter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.life.Model.User;
import com.example.life.chatActivities.Contacts;
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

public class ContactsActivity extends AppCompatActivity {

    private RecyclerView myContactsList;
    private DatabaseReference ContactsRef,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;
    private Toolbar mToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        myContactsList = findViewById(R.id.contactsList);
        myContactsList.setLayoutManager(new LinearLayoutManager(ContactsActivity.this));


        mToolbar = findViewById(R.id.contactsActivityToolbar);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        ContactsRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friends");



        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                        .setQuery(ContactsRef,Contacts.class)
                        .build();


        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adaptor =
                new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int position, @NonNull Contacts model) {

                        final String userIDs = getRef(position).getKey();
                        UsersRef.child(userIDs).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild("profileimage")){
                                    String userImage = dataSnapshot.child("profileimage").getValue().toString();
                                    String profileName = dataSnapshot.child("username").getValue().toString();
                                    String profileStatus = dataSnapshot.child("status").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);

                                    Picasso.get().load(userImage).placeholder(R.drawable.ic_launcher_background).into(holder.profileImage);
                                }else{
                                    String profileName = dataSnapshot.child("username").getValue().toString();
                                    String profileStatus = dataSnapshot.child("status").getValue().toString();

                                    holder.userName.setText(profileName);
                                    holder.userStatus.setText(profileStatus);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
                        View  view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_friends,viewGroup,false);
                        ContactsViewHolder viewHolder = new ContactsViewHolder(view);
                        return viewHolder;
                    }
                };

        myContactsList.setAdapter(adaptor);
        adaptor.startListening();



    }

    public static class ContactsViewHolder extends RecyclerView.ViewHolder{


        TextView userName, userStatus;
        CircleImageView profileImage;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.userNames);
            userStatus = itemView.findViewById(R.id.userStatus);
            profileImage = itemView.findViewById(R.id.usersProfileImage);
        }
    }
}