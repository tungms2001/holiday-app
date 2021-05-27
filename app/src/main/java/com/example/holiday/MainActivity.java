package com.example.holiday;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.holiday.fragment.HistoryFragment;
import com.example.holiday.fragment.NotificationFragment;
import com.example.holiday.fragment.ToursFragment;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private ViewPager vpMain;
    private TabLayout tlMain;
    private static final int FINISH = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vpMain = findViewById(R.id.vp_main);
        tlMain = findViewById(R.id.tl_main);

        tlMain.addTab(tlMain.newTab().setIcon(R.drawable.ic_history_clock));
        tlMain.addTab(tlMain.newTab().setIcon(R.drawable.ic_list));
        tlMain.addTab(tlMain.newTab().setIcon(R.drawable.ic_notification));

        PageAdapter adapter = new PageAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(new HistoryFragment());
        adapter.addFragment(new ToursFragment());
        adapter.addFragment(new NotificationFragment());

        vpMain.setAdapter(adapter);
        vpMain.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tlMain));
        tlMain.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(vpMain));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivityForResult(intent, FINISH);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FINISH)
            if (resultCode == RESULT_OK) {
                String result = data.getData().toString();
                if (result.equals("logout"))
                    finish();
            }
    }

    @Override
    public void onBackPressed() {
        Intent data = new Intent();
        data.setData(Uri.parse("close"));
        setResult(RESULT_OK, data);
        super.onBackPressed();
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