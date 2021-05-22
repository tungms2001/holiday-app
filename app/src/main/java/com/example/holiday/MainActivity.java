package com.example.holiday;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.holiday.fragment.HistoryFragment;
import com.example.holiday.fragment.NotificationFragment;
import com.example.holiday.fragment.ProfileFragment;
import com.example.holiday.fragment.ToursFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private ViewPager vpMain;
    private TabLayout tlMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpMain = findViewById(R.id.vp_main);
        tlMain = findViewById(R.id.tl_main);

        tlMain.addTab(tlMain.newTab().setIcon(R.drawable.ic_list));
        tlMain.addTab(tlMain.newTab().setIcon(R.drawable.ic_notification));
        tlMain.addTab(tlMain.newTab().setIcon(R.drawable.ic_history_clock));
        tlMain.addTab(tlMain.newTab().setIcon(R.drawable.ic_profile));

        PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new ToursFragment());
        adapter.addFragment(new NotificationFragment());
        adapter.addFragment(new HistoryFragment());
        adapter.addFragment(new ProfileFragment());

        vpMain.setAdapter(adapter);
        vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlMain));
        tlMain.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vpMain));
    }

    private static class PageAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragments = new Vector<>();

        public PageAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }
}