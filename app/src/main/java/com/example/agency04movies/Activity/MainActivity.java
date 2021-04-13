/*
Main starting activity which lists out top rated and popular movies. Contains ViewPager2 that contains 2 MovieItemFragments,
one for each list of results (popular and top rated movies). When results are fetch from network fragments are populated and created
and finally inserted in viewpager.
 */

package com.example.agency04movies.Activity;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.agency04movies.BroadcastReceivers.MainActivityNetworkReceiver;
import com.example.agency04movies.R;
import com.example.agency04movies.ViewPagerAdapter.ViewPagerAdapter;
import com.example.agency04movies.Viewmodel.MainActivityViewModel;
import com.example.agency04movies.databinding.ActivityMainBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Objects;

import static com.example.agency04movies.Constants.TAG;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ViewPager2 viewPager;
    private ViewPagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private MainActivityNetworkReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = binding.MainAppBar;
        this.setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.movies));

        viewPager = binding.MainViewPager;
        tabLayout = binding.MainTabLayout;
        binding.MainActivityProgressBar.setVisibility(View.VISIBLE);

        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        mainActivityViewModel.getThrowableLiveData().observe(this, throwable -> {
            receiver.setLockVariable(true);
            binding.MainActivityProgressBar.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.errorDescription));
            builder.setTitle(getString(R.string.errorTitle));
            builder.setPositiveButton(getString(R.string.dismiss), (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.d(TAG, "handleErrorGetListFromServer: error" + throwable.getMessage());
        });

        mainActivityViewModel.getFragments().observe(this, fragments -> {
            receiver.setFragmentArrayList(fragments);
            if (fragments.size() != 0) {
                receiver.setLockVariable(true);
            }
            pagerAdapter = new ViewPagerAdapter(this, fragments);
            viewPager.setAdapter(pagerAdapter);
            binding.MainActivityProgressBar.setVisibility(View.GONE);
            pagerAdapter.setFragments(fragments);

            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText(getString(R.string.popular));
                        break;
                    case 1:
                        tab.setText(getString(R.string.topRated));
                        break;
                }
            }).attach();
        });

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new MainActivityNetworkReceiver(mainActivityViewModel, this);
        this.registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}