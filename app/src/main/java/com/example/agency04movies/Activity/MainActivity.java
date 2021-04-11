/*
Main starting activity which lists out top rated and popular movies. Contains ViewPager2 that contains 2 MovieItemFragments,
one for each list of results (popular and top rated movies). When results are fetch from network fragments are populated and created
and finally inserted in viewpager.
 */

package com.example.agency04movies.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

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
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    //lockVariable is responsible for deciding the outcome of no internet textViews that is what variation of it will show depending
    // if there is data in fragments or there is no data
    private boolean lockVariable = false;
    private NetworkReceiver receiver;

    public class NetworkReceiver extends BroadcastReceiver {

        /*Detects changes in network connectivity and acts accordingly. If there is no connection shows a warning and if connection
        connection comes back removes warning and refreshes data if needed.*/

        private final MainActivityViewModel mainActivityViewModel;

        public NetworkReceiver(MainActivityViewModel mainActivityViewModel) {
            this.mainActivityViewModel = mainActivityViewModel;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                if (fragmentArrayList.size() == 0) {
                    binding.NoInternetLinearLayout.setVisibility(View.GONE);

                } else {
                    binding.NoInternetLinearLayout2.setVisibility(View.GONE);
                }
                if (fragmentArrayList.size() == 0 && lockVariable) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    mainActivityViewModel.getPopularMovies();
                }
            } else {
                if (fragmentArrayList.size() == 0) {
                    binding.NoInternetLinearLayout.setVisibility(View.VISIBLE);

                } else {
                    binding.NoInternetLinearLayout2.setVisibility(View.VISIBLE);
                }
            }
        }
    }

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
        binding.progressBar.setVisibility(View.VISIBLE);

        MainActivityViewModel mainActivityViewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        mainActivityViewModel.getThrowableLiveData().observe(this, throwable -> {
            lockVariable = true;
            binding.progressBar.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.errorDescription));
            builder.setTitle(getString(R.string.errorTitle));
            builder.setPositiveButton(getString(R.string.dismiss), (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.d(TAG, "handleErrorGetListFromServer: error" + throwable.getMessage());
        });

        mainActivityViewModel.getFragments().observe(this, fragments -> {
            fragmentArrayList = fragments;
            if (fragments.size() != 0) {
                lockVariable = true;
            }
            pagerAdapter = new ViewPagerAdapter(this, fragments);
            viewPager.setAdapter(pagerAdapter);
            binding.progressBar.setVisibility(View.GONE);
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
        receiver = new NetworkReceiver(mainActivityViewModel);
        this.registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}