/*Detects changes in network connectivity and acts accordingly. If there is no connection shows a warning and if connection
       connection comes back removes warning and refreshes data if needed.*/

package com.example.agency04movies.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.example.agency04movies.Activity.MovieDetailActivity;
import com.example.agency04movies.Models.MoviesItem;
import com.example.agency04movies.R;
import com.example.agency04movies.Viewmodel.MovieDetailActivityViewModel;

public class MovieDetailNetworkReceiver extends BroadcastReceiver {

    private final MovieDetailActivityViewModel movieDetailActivityViewModel;
    private final String movieID;
    private MoviesItem moviesItem = null;
    private final MovieDetailActivity movieDetailActivity;

    public MovieDetailNetworkReceiver(MovieDetailActivityViewModel movieDetailActivityViewModel, String movieID, MovieDetailActivity movieDetailActivity) {
        this.movieDetailActivityViewModel = movieDetailActivityViewModel;
        this.movieID = movieID;
        this.movieDetailActivity = movieDetailActivity;
    }

    public void setMoviesItem(MoviesItem moviesItem) {
        this.moviesItem = moviesItem;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conn.getActiveNetworkInfo();

        if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
            if (moviesItem == null) {
                movieDetailActivity.findViewById(R.id.MovieDetailNoInternetLinearLayout).setVisibility(View.GONE);
            } else {
                movieDetailActivity.findViewById(R.id.MovieDetailNoInternetLinearLayout2).setVisibility(View.GONE);
            }
            if (moviesItem == null) {
                movieDetailActivity.findViewById(R.id.MovieDetailProgressBar).setVisibility(View.VISIBLE);
                movieDetailActivityViewModel.getMovieDetailFromNetwork(movieID);
            }
        } else {
            if (moviesItem == null) {
                movieDetailActivity.findViewById(R.id.MovieDetailNoInternetLinearLayout).setVisibility(View.VISIBLE);
            } else {
                movieDetailActivity.findViewById(R.id.MovieDetailNoInternetLinearLayout2).setVisibility(View.VISIBLE);
            }
        }
    }
}
