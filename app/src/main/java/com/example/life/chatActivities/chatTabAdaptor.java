package com.example.life.chatActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.life.fragments.Chats;
import com.example.life.fragments.Group;
import com.example.life.fragments.Store;

public class chatTabAdaptor extends FragmentPagerAdapter {
    public chatTabAdaptor(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                Chats chats = new Chats();
                return chats;
            case 2:
                Store store = new Store();
                return store;
            case 1:
                Group group = new Group();
                return group;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        switch (position){
            case 0:
                return "Chats";
            case 1:
                return "Groups";
            case 2:
                return "Store";
            default:
                return null;
        }

    }
}
