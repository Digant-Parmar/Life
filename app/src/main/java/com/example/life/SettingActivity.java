package com.example.life;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.life.chatActivities.ChatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private EditText edtUsername, edtUserStatus;
    private Button btnUpdateStatus;
    private CircleImageView cimgUserProfileImage;
    private String currentUserId;


    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();

        InitializeFields();
        btnUpdateStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        RetrieveUserInfo();
    }


    private void InitializeFields() {

        edtUsername = findViewById(R.id.setUserName);
        edtUserStatus = findViewById(R.id.setProfileStatus);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        cimgUserProfileImage = findViewById(R.id.profileImage);


    }
    private void UpdateSettings(){
        String setUserName = edtUsername.getText().toString();
        String setStatus = edtUserStatus.getText().toString();
        
        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please Provide Your UserName", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,String >profileMap = new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("username",setUserName);
            profileMap.put("status",setStatus);

            RootRef.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToChatActivity();
                                Toast.makeText(SettingActivity.this, "Profile Updated Succesfully", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(SettingActivity.this, "Error "+task.getException().toString(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    private void SendUserToChatActivity(){
        startActivity(new Intent(SettingActivity.this, ChatActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }

    private void RetrieveUserInfo() {

        RootRef.child("Users").child(currentUserId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists() && dataSnapshot.hasChild("username") && dataSnapshot.hasChild("image") && dataSnapshot.hasChild("status")){

                            String retrieveUsername = dataSnapshot.child("username").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveUserProfileImg = dataSnapshot.child("profileimage").getValue().toString();


                            edtUsername.setText(retrieveUsername);
                            edtUserStatus.setText(retrieveUserStatus);


                        }else if(dataSnapshot.exists() && dataSnapshot.hasChild("username") && dataSnapshot.hasChild("image")){
                            String retrieveUsername = dataSnapshot.child("username").getValue().toString();
                            String retrieveUserProfileImg = dataSnapshot.child("profileimage").getValue().toString();

                            edtUsername.setText(retrieveUsername);
                        }else if (dataSnapshot.exists() && dataSnapshot.hasChild("username")  && dataSnapshot.hasChild("status")){

                            String retrieveUsername = dataSnapshot.child("username").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();

                            edtUsername.setText(retrieveUsername);
                            edtUserStatus.setText(retrieveUserStatus);
                        }
                        else if(dataSnapshot.exists() && dataSnapshot.hasChild("username") ){
                            String retrieveUsername = dataSnapshot.child("username").getValue().toString();
                            edtUsername.setText(retrieveUsername);
                        }
                        else {
                            Toast.makeText(SettingActivity.this, "Please Provide Your Profile Information", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


}