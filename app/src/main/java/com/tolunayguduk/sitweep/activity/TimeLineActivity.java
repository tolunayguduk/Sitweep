package com.tolunayguduk.sitweep.activity;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.tolunayguduk.sitweep.R;
import com.tolunayguduk.sitweep.fragments.anasayfaFragment;
import com.tolunayguduk.sitweep.fragments.bildirimFragment;
import com.tolunayguduk.sitweep.fragments.globalFragment;
import com.tolunayguduk.sitweep.fragments.mekanFragment;
import com.tolunayguduk.sitweep.fragments.profilFragment;

import java.util.ArrayList;
import java.util.List;

public class TimeLineActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);








        authControl();
        initComponent();
        registered();
    }

    private void authControl() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent i = new Intent(TimeLineActivity.this,LoginActivity.class);
            startActivity(i);
        }
    }

    @Override
    protected void onResume() {
        authControl();
        super.onResume();
    }

    private void initComponent() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
    }

    private void registered() {
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setIcon(android.R.drawable.ic_menu_myplaces);
        tabLayout.getTabAt(1).setIcon(android.R.drawable.ic_menu_myplaces);
        tabLayout.getTabAt(2).setIcon(android.R.drawable.ic_menu_myplaces);
        tabLayout.getTabAt(3).setIcon(android.R.drawable.ic_menu_myplaces);
        tabLayout.getTabAt(4).setIcon(android.R.drawable.ic_menu_myplaces);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu, this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line, menu);
        return true;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new anasayfaFragment(), "");
        adapter.addFragment(new globalFragment(), "");
        adapter.addFragment(new mekanFragment(), "");
        adapter.addFragment(new bildirimFragment(), "");
        adapter.addFragment(new profilFragment(), "");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bank:
                Intent i = new Intent(TimeLineActivity.this,StoreActivity.class);
                startActivity(i);
                return true;

            case R.id.message:
                Intent j = new Intent(TimeLineActivity.this,DirectMessageActivity.class);
                startActivity(j);
                return true;

            case R.id.action_search:
                Intent k = new Intent(TimeLineActivity.this,SearchActivity.class);
                startActivity(k);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
