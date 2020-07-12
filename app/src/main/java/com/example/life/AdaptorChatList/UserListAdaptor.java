package com.example.life.AdaptorChatList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.life.Model.User;
import com.example.life.R;

import java.util.List;

public class UserListAdaptor extends RecyclerView.Adapter {


    private Context context;
    private List<User>users;

    public UserListAdaptor(Context context,List<User>users){

        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_items,parent,false);


        return new UserListAdaptor.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolder myholder = (ViewHolder) holder;
        User user = users.get(position);
        myholder.txtusername.setText(user.getUsername());
//        if(user.getImageURL().equals("default")){
//            myholder.profileImage.setImageResource(R.mipmap.ic_launcher);
//        }else {
//            Glide.with(context).load(user.getImageURL()).into(myholder.profileImage);
//        }

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtusername;
        private ImageView profileImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtusername = itemView.findViewById(R.id.listName);
            profileImage = itemView.findViewById(R.id.listProfileImage);



        }
    }


}
