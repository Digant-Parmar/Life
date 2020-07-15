package com.example.life;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private String receiverUserID, currentState, senderUserID;
    private CircleImageView userProfileImage;
    private TextView userProfileName, userProfileStatus;
    private Button btnSendMessageRequestButton,btnDeclineMessageRequestButton;

    private DatabaseReference UserRef,ChatRequestRef,ContactRef;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        receiverUserID = getIntent().getExtras().get("visitUserID").toString();
        UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ContactRef = FirebaseDatabase.getInstance().getReference().child("Contacts");

        userProfileImage = findViewById(R.id.visitProfileImage);
        userProfileName = findViewById(R.id.visitProfileUserName);
        userProfileStatus = findViewById(R.id.visitProfileStatus);
        btnSendMessageRequestButton = findViewById(R.id.sendMessageRequestButton);
        currentState = "new";
        mAuth = FirebaseAuth.getInstance();
        senderUserID = mAuth.getCurrentUser().getUid();
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Chat Request");
        btnDeclineMessageRequestButton = findViewById(R.id.declineMessageRequestButton);


        RetrieveUserInfo();


    }

    private void RetrieveUserInfo() {

        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()  &&  dataSnapshot.hasChild("profileimage")){


                    String userImage = dataSnapshot.child("profileimage").getValue().toString();
                    String userName = dataSnapshot.child("username").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    Picasso.get().load(userImage).placeholder(R.drawable.ic_launcher_background).into(userProfileImage);
                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);

                    ManageChatRequest();

                }else {
                    String userName = dataSnapshot.child("username").getValue().toString();
                    String userStatus = dataSnapshot.child("status").getValue().toString();

                    userProfileName.setText(userName);
                    userProfileStatus.setText(userStatus);
                    ManageChatRequest();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void ManageChatRequest() {

        ChatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(receiverUserID)){
                            String requesttype = dataSnapshot.child(receiverUserID).child("requesttype").getValue().toString();
                            if(requesttype.equals("sent")){
                                currentState = "requestSent";
                                btnSendMessageRequestButton.setText("Cancel Chat Request");
                            }else if(requesttype.equals("received")){
                                currentState = "requestReceived";
                                btnSendMessageRequestButton.setText("Accept Chat Request");

                                btnDeclineMessageRequestButton.setVisibility(View.VISIBLE);
                                btnDeclineMessageRequestButton.setEnabled(true);

                                btnDeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        CancelChatRequest();

                                    }
                                });
                            }
                        }else {
                            ContactRef.child(senderUserID)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.hasChild(receiverUserID)){
                                                currentState = "friends";
                                                btnSendMessageRequestButton.setText("remove");
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
        if(!senderUserID.equals(receiverUserID)){

            btnSendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   // btnSendMessageRequestButton.setEnabled(false);
                    if(currentState.equals("new")){
                        SendChatRequest();
                    }
                    if(currentState.equals("requestSent")){
                        CancelChatRequest();
                    }
                    if(currentState.equals("requestReceived")){
                        acceptChatRequest();
                    }
                    if(currentState.equals("friends")){
                        RemoveSpecificContact();
                    }
                }
            });

        }else {
            btnSendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }




    private void RemoveSpecificContact() {

        ContactRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ContactRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                btnSendMessageRequestButton.setEnabled(true);
                                                currentState = "new";
                                                btnSendMessageRequestButton.setText("Send Message");
                                                btnDeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                btnDeclineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });

    }





    private void acceptChatRequest() {

        ContactRef.child(senderUserID).child(receiverUserID)
                .child("Contacts").setValue("saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ContactRef.child(receiverUserID).child(senderUserID)
                                    .child("Contacts").setValue("saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                                ChatRequestRef.child(senderUserID).child(receiverUserID)
                                                        .removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if(task.isSuccessful()){

                                                                    ChatRequestRef.child(receiverUserID).child(senderUserID)
                                                                            .removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                                    btnSendMessageRequestButton.setEnabled(true);
                                                                                    currentState = "friends";
                                                                                    btnSendMessageRequestButton.setText("Remove");
                                                                                    btnDeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                                                    btnDeclineMessageRequestButton.setEnabled(false);
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }
                                        }
                                    });

                        }
                    }
                });

    }

    private void CancelChatRequest() {

        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                btnSendMessageRequestButton.setEnabled(true);
                                                currentState = "new";
                                                btnSendMessageRequestButton.setText("Send Message");
                                                btnDeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                btnDeclineMessageRequestButton.setEnabled(false);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendChatRequest() {

        ChatRequestRef.child(senderUserID).child(receiverUserID)
                .child("requesttype").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            ChatRequestRef.child(receiverUserID).child(senderUserID)
                                    .child("requesttype").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                btnSendMessageRequestButton.setEnabled(true);
                                                currentState = "requestSent";
                                                btnSendMessageRequestButton.setText("Cancel Chat Request");
                                            }
                                        }
                                    });
                        }
                    }
                });

    }


}