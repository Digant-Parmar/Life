package com.example.life.chatActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.arch.core.executor.TaskExecutor;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.life.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.internal.cache.CacheInterceptor;

public class PrivateChatActivity extends AppCompatActivity {

    private String messageReceiverID, messageReceiverName, messageReceiverImage, messageSendID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;

    private Toolbar ChatToolBar;

    private Button btnSendMessage;
    private EditText messageInputText;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager linearLayoutManager;
    private MessageAdaptor messageAdaptor;
    private RecyclerView usersMessagesList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_chat);


        mAuth = FirebaseAuth.getInstance();
        messageSendID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        messageReceiverID  = getIntent().getExtras().get("visitUserId").toString();
        messageReceiverName = getIntent().getExtras().get("visitUserName").toString();
        messageReceiverImage =  getIntent().getExtras().get("visitImage").toString();
        InitializeControllers();

        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.ic_launcher_background).into(userImage);

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
            }
        });

    }

    private void InitializeControllers() {

        ChatToolBar = findViewById(R.id.privateChatToolBar);
        setSupportActionBar(ChatToolBar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        LayoutInflater layoutInflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = layoutInflater.inflate(R.layout.custom_chat_bar,null);
        actionBar.setCustomView(actionBarView);

        userImage = findViewById(R.id.customProfileImage);
        userName = findViewById(R.id.customProfileName);
        userLastSeen = findViewById(R.id.customUserLastSeen);

        btnSendMessage = findViewById(R.id.btnSendMessage);
        messageInputText = findViewById(R.id.inputMessage);

        messageAdaptor = new MessageAdaptor(messagesList);
        usersMessagesList = findViewById(R.id.privateMessagesListOfUsers);
        linearLayoutManager = new LinearLayoutManager(this);
        usersMessagesList.setLayoutManager(linearLayoutManager);
        usersMessagesList.setAdapter(messageAdaptor);

    }

    @Override
    protected void onStart() {
        super.onStart();
        RootRef.child("Messages").child(messageSendID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Messages messages= dataSnapshot.getValue(Messages.class);
                        messagesList.add(messages);
                        messageAdaptor.notifyDataSetChanged();

                        usersMessagesList.smoothScrollToPosition(usersMessagesList.getAdapter().getItemCount());
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void SendMessage(){

        String messageText = messageInputText.getText().toString();

        if(TextUtils.isEmpty(messageText)){
            Toast.makeText(this, "Enter The Message", Toast.LENGTH_SHORT).show();
        }
        else{

           String messageSendRef = "Messages/"+ messageSendID +"/"+messageReceiverID;
           String messageReceiveRef = "Messages/"+ messageReceiverID +"/"+messageSendID;


            DatabaseReference userMessageKeyRef = RootRef.child("Message")
                    .child(messageSendID).child(messageReceiverID).push();

            String messagePushID= userMessageKeyRef.getKey();

            Map messageTextBody = new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSendID);

            Map messageBodyDetails = new HashMap();
            messageBodyDetails.put(messageSendRef+"/"+messagePushID,messageTextBody);
            messageBodyDetails.put(messageReceiveRef+"/"+messagePushID,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if(task.isSuccessful()){
                        Toast.makeText(PrivateChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(PrivateChatActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                    messageInputText.setText("");
                }
            });


        }

    }
}