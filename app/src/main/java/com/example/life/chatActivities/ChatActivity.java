package com.example.life.chatActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;


import com.example.life.LoginSignIn.StartUp;
import com.example.life.R;
import com.example.life.SettingActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatActivity extends AppCompatActivity {

    private Toolbar maintoolbar;
    private TabLayout maintabLayout;
    private chatTabAdaptor chattabAdaptor;
    private ViewPager mainViewPager;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    private FirebaseUser currentUser;




    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser!=null){
            VerifyUserExistance();
        }
    }

    private void VerifyUserExistance() {
        String currentUserID = mAuth.getCurrentUser().getUid();
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("username").exists()){
                    Toast.makeText(ChatActivity.this, "WelCome..", Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(new Intent(ChatActivity.this, SettingActivity.class)
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        setTitle("");

        mAuth = FirebaseAuth.getInstance();
        currentUser= mAuth.getCurrentUser();
        RootRef = FirebaseDatabase.getInstance().getReference();





        maintoolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(maintoolbar);

        mainViewPager = findViewById(R.id.mainViewPage);
        chattabAdaptor = new chatTabAdaptor(getSupportFragmentManager(),1);
        mainViewPager.setAdapter(chattabAdaptor);

        maintabLayout = findViewById(R.id.mainTab);
        maintabLayout.setupWithViewPager(mainViewPager,true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()){

        case R.id.mnSetting :
            Toast.makeText(ChatActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            sendUsersToSettingActivity();
            return true;
        case R.id.logout:
             FirebaseAuth.getInstance().signOut();
             startActivity(new Intent(ChatActivity.this, StartUp.class)
                     .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
             finish();
             return true;

            case R.id.createGroup:
                RequestNewGroup();
        }

        return super.onOptionsItemSelected(item);


}

    private void RequestNewGroup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name: ");

        final EditText groupNameField = new EditText(ChatActivity.this);
        groupNameField.setHint("e.g Friends For Life");
        builder.setView(groupNameField);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameField.getText().toString();

                if(TextUtils.isEmpty(groupName)){
                    Toast.makeText(ChatActivity.this, "Please Enter The Group Name", Toast.LENGTH_SHORT).show();
                }else{
                    CreateNewGroup(groupName);
                }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });


        builder.show();


    }

    private void sendUsersToSettingActivity() {
        startActivity(new Intent(ChatActivity.this, SettingActivity.class)
             );
    }


    private void CreateNewGroup(final String groupName){
        RootRef.child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(ChatActivity.this, "Group "+groupName+" created successfully", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }


}