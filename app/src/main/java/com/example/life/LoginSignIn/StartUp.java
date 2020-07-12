package com.example.life.LoginSignIn;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.example.life.chatActivities.ChatActivity;
import com.example.life.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;

public class StartUp extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private TabAdapter tabAdaptor;



    @Override
    protected void onStart() {
        super.onStart();

        // Auto Login if has logged in before
        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            startActivity(new Intent(StartUp.this, ChatActivity.class));
            finish();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);




        toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewPage);
        tabAdaptor = new TabAdapter(getSupportFragmentManager(),1);
        viewPager.setAdapter(tabAdaptor);

        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager,true);
        if(FirebaseAuth.getInstance().getCurrentUser() !=null){
            startActivity(new Intent(StartUp.this, ChatActivity.class));
            finish();
        }

    }




}