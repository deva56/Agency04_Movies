/*Detects changes in network connectivity and acts accordingly. If there is no connection shows a warning and if connection
       connection comes back removes warning and refreshes data if needed.*/

package com.example.agency04movies.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.example.agency04movies.Activity.SimilarMoviesActivity;
import com.example.agency04movies.Models.MoviesListItem;
import com.example.agency04movies.R;
import com.example.agency04movies.Viewmodel.SimilarMoviesActivityViewModel;

import static com.example.agency04movies.Constants.api_key;

public class SimilarMoviesNetworkReceiver extends BroadcastReceiver {
    private final SimilarMoviesActivityViewModel similarMoviesActivityViewModel;
    private final String movieID;
    private MoviesListItem moviesListItem;
    //variable to detect when should data be re-fetched if there is internet connection again
    private boolean lockVariable = false;
    private final SimilarMoviesActivity similarMoviesActivity;

    public SimilarMoviesNetworkReceiver(SimilarMoviesActivityViewModel similarMoviesActivityViewModel, String movieID, SimilarMoviesActivity similarMoviesActivity) {
        this.similarMoviesActivityViewModel = similarMoviesActivityViewModel;
        this.movieID = movieID;
        this.similarMoviesActivity = similarMoviesActivity;
    }

    public void setLockVariable(boolean lockVariable) {
        this.lockVariable = lockVariable;
    }

    public void setMoviesListItem(MoviesListItem moviesListItem) {
        this.moviesListItem = moviesListItem;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        //multiple if checks to detect which view to show and what work to do if needed (e.g. refreshing data)
        if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
            if (moviesListItem != null) {
                if (moviesListItem.getMoviesItemList().size() == 0) {
                    similarMoviesActivity.findViewById(R.id.SimilarMoviesNoInternetLinearLayout).setVisibility(View.GONE);
                } else if (moviesListItem.getMoviesItemList().size() == 0 && lockVariable) {
                    similarMoviesActivity.findViewById(R.id.SimilarMoviesProgressBar).setVisibility(View.VISIBLE);
                    similarMoviesActivityViewModel.getSimilarMovies(movieID, api_key);
                } else {
                    similarMoviesActivity.findViewById(R.id.SimilarMoviesNoInternetLinearLayout2).setVisibility(View.GONE);
                }
            } else {
                if (lockVariable) {
                    similarMoviesActivity.findViewById(R.id.SimilarMoviesProgressBar).setVisibility(View.VISIBLE);
                    similarMoviesActivityViewModel.getSimilarMovies(movieID, api_key);
                }
                similarMoviesActivity.findViewById(R.id.SimilarMoviesNoInternetLinearLayout).setVisibility(View.GONE);
            }
        } else {
            if (moviesListItem != null) {
                if (moviesListItem.getMoviesItemList().size() == 0) {
                    similarMoviesActivity.findViewById(R.id.SimilarMoviesNoInternetLinearLayout).setVisibility(View.VISIBLE);
                } else {
                    similarMoviesActivity.findViewById(R.id.SimilarMoviesNoInternetLinearLayout2).setVisibility(View.VISIBLE);
                }
            } else {
                similarMoviesActivity.findViewById(R.id.SimilarMoviesNoInternetLinearLayout).setVisibility(View.VISIBLE);
            }
        }
    }
}
