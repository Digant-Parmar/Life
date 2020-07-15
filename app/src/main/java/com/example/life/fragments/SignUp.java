package com.example.life.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.life.chatActivities.ChatActivity;
import com.example.life.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;


public class SignUp extends Fragment {


    private Button btnSignUp;
    private MaterialEditText edtEmail,edtUsername,edtPass;
    private ProgressDialog loadingBar;

    FirebaseAuth auth;
    DatabaseReference reference;





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v =inflater.inflate(R.layout.fragment_sign_up, container, false);

        btnSignUp = v.findViewById(R.id.btnSignUp);
        edtEmail = v.findViewById(R.id.edtEmailSignUp);
        edtPass = v.findViewById(R.id.edtPasswordSignUp);
        edtUsername = v.findViewById(R.id.edtUsernameSignUp);
        auth  = FirebaseAuth.getInstance();

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_user = edtUsername.getText().toString();
                String txt_email = edtEmail.getText().toString();
                String txt_pass = edtPass.getText().toString();

                if(TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_pass) || TextUtils.isEmpty(txt_user)){
                    //Toast.makeText(SignUp,"All Field Should be Filled",Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(),"All Field Should be Filled",Toast.LENGTH_SHORT).show();

                }else if(txt_pass.length()<6){

                   Toast.makeText(getActivity(),"Password Should be at least of length 6",Toast.LENGTH_SHORT).show();
                }else {

                    register(txt_user,txt_email,txt_pass);
                }

            }
        });

        // Inflate the layout for this fragment
        return v;
    }


    private void register(final String username,String email, String pass){
        auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            try
                            {
                                throw task.getException();
                            }
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                Log.d("TAG", "onComplete: weak_password");

                                // TODO: take your actions!
                                Toast.makeText(getActivity(), "Password is weak enter a stronger password", Toast.LENGTH_SHORT).show();


                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d("TAG", "onComplete: malformed_email");

                                // TODO: Take your action
                                Toast.makeText(getActivity(), "Enter a Valid Email", Toast.LENGTH_SHORT).show();

                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Log.d("TAG", "onComplete: exist_email");

                                // TODO: Take your action
                                Toast.makeText(getActivity(), "Email entered already exists", Toast.LENGTH_SHORT).show();

                            }
                            catch (Exception e)
                            {
                                Log.d("TAG", "onComplete: " + e.getMessage());
                            }


                        }else {
                            loadingBar = new ProgressDialog(getActivity());
                            loadingBar.setTitle("Creating New Account");
                            loadingBar.setMessage("Please wait while we are creating a new account for you...");
                            loadingBar.setCanceledOnTouchOutside(true);
                            loadingBar.show();
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();

                            reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

                            HashMap<String,String > hashMap = new HashMap<>();
                            hashMap.put("uid",userid);
                            hashMap.put("username",username);
                            hashMap.put("status","WhatsApp is boring Lets have Some Fun While Chatting");
                            //hashMap.put("imageURL","default");

                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        loadingBar.dismiss();
                                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        getActivity().finish();

                                    }
                                    loadingBar.dismiss();
                                }
                            });
                        }

                    }
                });

    }

}