/*Detects changes in network connectivity and acts accordingly. If there is no connection shows a warning and if connection
        connection comes back removes warning and refreshes data if needed.*/

package com.example.agency04movies.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.example.agency04movies.Activity.MainActivity;
import com.example.agency04movies.R;
import com.example.agency04movies.Viewmodel.MainActivityViewModel;

import java.util.ArrayList;

public class MainActivityNetworkReceiver extends BroadcastReceiver {

    private final MainActivityViewModel mainActivityViewModel;
    //lockVariable is responsible for deciding the outcome of no internet textViews that is what variation of it will show depending
    // if there is data in fragments or there is no data
    private boolean lockVariable = false;
    private ArrayList<Fragment> fragmentArrayList = new ArrayList<>();
    private final MainActivity mainActivity;

    public MainActivityNetworkReceiver(MainActivityViewModel mainActivityViewModel, MainActivity mainActivity) {
        this.mainActivityViewModel = mainActivityViewModel;
        this.mainActivity = mainActivity;
    }

    public void setLockVariable(boolean lockVariable) {
        this.lockVariable = lockVariable;
    }

    public void setFragmentArrayList(ArrayList<Fragment> fragmentArrayList) {
        this.fragmentArrayList = fragmentArrayList;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
            if (fragmentArrayList.size() == 0) {
                mainActivity.findViewById(R.id.MainActivityNoInternetLinearLayout).setVisibility(View.GONE);
            } else {
                mainActivity.findViewById(R.id.MainActivityNoInternetLinearLayout2).setVisibility(View.GONE);
            }
            if (fragmentArrayList.size() == 0 && lockVariable) {
                mainActivity.findViewById(R.id.MainActivityProgressBar).setVisibility(View.VISIBLE);
                mainActivityViewModel.getPopularMovies();
            }
        } else {
            if (fragmentArrayList.size() == 0) {
                mainActivity.findViewById(R.id.MainActivityNoInternetLinearLayout).setVisibility(View.VISIBLE);

            } else {
                mainActivity.findViewById(R.id.MainActivityNoInternetLinearLayout2).setVisibility(View.VISIBLE);
            }
        }
    }
}

