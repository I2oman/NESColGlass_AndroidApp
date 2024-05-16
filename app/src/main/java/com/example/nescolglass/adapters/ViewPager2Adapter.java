package com.example.nescolglass.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class ViewPager2Adapter extends FragmentStateAdapter {
    // ArrayList to hold the fragments to be displayed in the ViewPager
    ArrayList<Fragment> fragments;

    // Constructor to initialize the adapter with the fragments
    public ViewPager2Adapter(@NonNull FragmentActivity fragmentActivity, ArrayList<Fragment> fragments) {
        super(fragmentActivity);
        this.fragments = fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Return the fragment corresponding to the specified position
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        // Return the total number of fragments in the ArrayList
        return fragments.size();
    }
}
