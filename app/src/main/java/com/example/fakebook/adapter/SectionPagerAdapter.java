package com.example.fakebook.adapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.fakebook.fragment.HomeFragment;
import com.example.fakebook.fragment.MessageFragment;
import com.example.fakebook.fragment.NotificationFragment;
import com.example.fakebook.fragment.PersonalityFragment;
import com.example.fakebook.fragment.RequestFragment;

public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // position + 1 vì position bắt đầu từ số 0.
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return new HomeFragment();
            case 1: // Fragment # 0 - This will show FirstFragment different title
                return new RequestFragment();
            case 2: // Fragment # 1 - This will show SecondFragment
                return new PersonalityFragment();
            case 3: // Fragment # 1 - This will show SecondFragment
                return new NotificationFragment();
            case 4:
                return new MessageFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 5;
    }

    @Override
    public CharSequence getPageTitle(int position) {
//        switch (position) {
//            case 0:
//                return "Home";
//            case 1:
//                return "Friend Request";
//            case 2:
//                return "Personality";
//            case 3:
//                return "Notification";
//            case 4:
//                return "Game";
//        }
        return null;
    }
}