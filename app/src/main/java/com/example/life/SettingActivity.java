package com.example.life;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.life.chatActivities.ChatActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.PriorityQueue;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    private EditText edtUsername, edtUserStatus;
    private Button btnUpdateStatus;
    private CircleImageView cimgUserProfileImage;
    private String currentUserId;


    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private static final int GalleryPick=1;

    private StorageReference UserProfileImagesRef;
    private ProgressDialog loadingBar;

    private   String downloadUrl;

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
        cimgUserProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)//Sett Coordinate Ratio Here
                        .setAspectRatio(1,1)
                        .start(SettingActivity.this);

            }
        });
    }


    private void InitializeFields() {

        edtUsername = findViewById(R.id.setUserName);
        edtUserStatus = findViewById(R.id.setProfileStatus);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        cimgUserProfileImage = findViewById(R.id.profileImage);
        UserProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        loadingBar = new ProgressDialog(this);


    }

//For getting Image From the Gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                loadingBar.setTitle("Profile Image");
                loadingBar.setMessage("Your Profile Image is Updating...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                final StorageReference filePath = UserProfileImagesRef.child(currentUserId + ".jpg");

                final Uri resultUri = result.getUri();


                UploadTask uploadTask= filePath.putFile(resultUri);
                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri downloadUri = task.getResult();
                            Toast.makeText(SettingActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                            if (downloadUri != null) {

                                downloadUrl = downloadUri.toString(); //YOU WILL GET THE DOWNLOAD URL HERE !!!!
                                RootRef.child("Users").child(currentUserId).child("profileimage").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        loadingBar.dismiss();
                                        if(!task.isSuccessful()){
                                            String error=task.getException().toString();
                                            Toast.makeText(SettingActivity.this,"Error : "+error,Toast.LENGTH_LONG).show();
                                        }else{

                                        }
                                    }
                                });
                            }

                        } else {
                            // Handle failures
                            // ...
                            Toast.makeText(SettingActivity.this,"Error",Toast.LENGTH_LONG).show();
                            loadingBar.dismiss();
                        }
                    }
                });


            }
        }
    }








    private void UpdateSettings(){
        String setUserName = edtUsername.getText().toString();
        String setStatus = edtUserStatus.getText().toString();
        String setProfileImage = downloadUrl;
        
        if(TextUtils.isEmpty(setUserName)){
            Toast.makeText(this, "Please Provide Your UserName", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,String >profileMap = new HashMap<>();
            profileMap.put("uid",currentUserId);
            profileMap.put("username",setUserName);
            profileMap.put("status",setStatus);
            profileMap.put("profileimage",setProfileImage);


            RootRef.child("Users").child(currentUserId).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                SendUserToChatActivity();
                                Toast.makeText(SettingActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
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
                        if(dataSnapshot.exists() && dataSnapshot.hasChild("username") && dataSnapshot.hasChild("profileimage") && dataSnapshot.hasChild("status")){

                            String retrieveUsername = dataSnapshot.child("username").getValue().toString();
                            String retrieveUserStatus = dataSnapshot.child("status").getValue().toString();
                            String retrieveUserProfileImg = dataSnapshot.child("profileimage").getValue().toString();


                            edtUsername.setText(retrieveUsername);
                            edtUserStatus.setText(retrieveUserStatus);
                            Picasso.get().load(retrieveUserProfileImg).into(cimgUserProfileImage);


                        }else if(dataSnapshot.exists() && dataSnapshot.hasChild("username") && dataSnapshot.hasChild("profileimage")){
                            String retrieveUsername = dataSnapshot.child("username").getValue().toString();
                            String retrieveUserProfileImg = dataSnapshot.child("profileimage").getValue().toString();

                            Picasso.get().load(retrieveUserProfileImg).into(cimgUserProfileImage);

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