package com.example.life.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.life.R;
import com.example.life.chatActivities.GroupChatActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class Group extends Fragment {

    private View view;
    private ListView groupListView;
    private ArrayAdapter<String>arrayAdapter;
    private ArrayList<String>listOfGroups = new ArrayList<>();

    private DatabaseReference GroupRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         view  = inflater.inflate(R.layout.fragment_group, container, false);

         GroupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

         InitializeField();

         RetrieveAndDisplayGroups();
         groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                 String currentGroupName = adapterView.getItemAtPosition(position).toString();

                 Intent groupChatIntent = new Intent(getContext(), GroupChatActivity.class);
                 groupChatIntent.putExtra("groupName",currentGroupName);
                 startActivity(groupChatIntent);
             }
         });

        return view;
    }


    private void InitializeField() {

        groupListView = (ListView) view.findViewById(R.id.groupListView);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,listOfGroups);
        groupListView.setAdapter(arrayAdapter);
    }

    private void RetrieveAndDisplayGroups() {

        GroupRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String>set = new HashSet<>();

                Iterator iterator = dataSnapshot.getChildren().iterator();

                while (iterator.hasNext()){

                    set.add(((DataSnapshot)iterator.next()).getKey());

                }

                listOfGroups.clear();
                listOfGroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}