/*
Activity with details of selected movie. After fetching from network is fills up views with values from it's model.
Contains button for finding similar movies if there are any.
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.agency04movies.Models.MoviesItem;
import com.example.agency04movies.Models.MoviesItemGenreIDs;
import com.example.agency04movies.R;
import com.example.agency04movies.Viewmodel.MovieDetailActivityViewModel;
import com.example.agency04movies.Viewmodel.MovieDetailViewModelFactory;
import com.example.agency04movies.databinding.ActivityMovieDetailBinding;

import java.util.List;

import static com.example.agency04movies.Constants.SELECTED_MOVIE_ID;
import static com.example.agency04movies.Constants.SELECTED_MOVIE_TITLE;
import static com.example.agency04movies.Constants.TAG;
import static com.example.agency04movies.Constants.image_base_url;

public class MovieDetailActivity extends AppCompatActivity {

    private ActivityMovieDetailBinding binding;
    String currentMovieID;
    String currentMovieTitle;
    private MoviesItem moviesItem;
    private NetworkReceiver receiver;

    public class NetworkReceiver extends BroadcastReceiver {

        /*Detects changes in network connectivity and acts accordingly. If there is no connection shows a warning and if connection
       connection comes back removes warning and refreshes data if needed.*/

        private final MovieDetailActivityViewModel movieDetailActivityViewModel;
        private final String movieID;

        public NetworkReceiver(MovieDetailActivityViewModel movieDetailActivityViewModel, String movieID) {
            this.movieDetailActivityViewModel = movieDetailActivityViewModel;
            this.movieID = movieID;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = conn.getActiveNetworkInfo();

            if (networkInfo != null && (networkInfo.getType() == ConnectivityManager.TYPE_WIFI || networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)) {
                if (moviesItem == null) {
                    binding.NoInternetLinearLayout.setVisibility(View.GONE);
                } else {
                    binding.NoInternetLinearLayout2.setVisibility(View.GONE);
                }
                if (moviesItem == null) {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    movieDetailActivityViewModel.getMovieDetailFromNetwork(movieID);
                }
            } else {
                if (moviesItem == null) {
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
        binding = ActivityMovieDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = binding.MovieDetailAppBar;
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.MovieDetailCardView.setVisibility(View.GONE);

        currentMovieID = getIntent().getStringExtra(SELECTED_MOVIE_ID);
        currentMovieTitle = getIntent().getStringExtra(SELECTED_MOVIE_TITLE);

        MovieDetailActivityViewModel movieDetailActivityViewModel
                = new ViewModelProvider(this, new MovieDetailViewModelFactory(getApplication(), currentMovieID))
                .get(MovieDetailActivityViewModel.class);

        movieDetailActivityViewModel.getThrowableLiveData().observe(this, throwable -> {
            binding.progressBar.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.errorDescription));
            builder.setTitle(getString(R.string.errorTitle));
            builder.setPositiveButton(getString(R.string.dismiss), (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.d(TAG, "handleErrorGetMovieDetailFromServer: " + throwable.getMessage());
        });

        movieDetailActivityViewModel.getMoviesItemLiveData().observe(this, moviesItem -> {
            this.moviesItem = moviesItem;
            List<MoviesItemGenreIDs> moviesItemGenreIDsList = moviesItem.getMoviesItemGenreObject();
            Glide.with(this).load(image_base_url + moviesItem.getBackdrop_path()).placeholder(R.drawable.ic_no_image_available).centerCrop().into(binding.BackdropImageView);
            Glide.with(this).load(image_base_url + moviesItem.getPoster_path()).placeholder(R.drawable.ic_no_image_available).centerCrop().into(binding.PosterImageView);
            binding.TitleTextView.setText(getString(R.string.title) + " " + moviesItem.getTitle());
            binding.ReleaseDateTextView.setText(getString(R.string.releaseDate) + " " + moviesItem.getRelease_date());
            binding.OverviewText.setText(moviesItem.getOverview());
            StringBuilder genres = new StringBuilder(getString(R.string.genres) + " ");
            for (int i = 0; i < moviesItemGenreIDsList.size(); i++) {
                if (i != moviesItemGenreIDsList.size() - 1) {
                    genres.append(moviesItemGenreIDsList.get(i).getName()).append(", ");
                } else {
                    genres.append(moviesItemGenreIDsList.get(i).getName());
                }
            }
            binding.GenresTextView.setText(genres.toString());
            binding.MovieDetailsAppBarTextView.setText(moviesItem.getTitle());
            binding.progressBar.setVisibility(View.GONE);
            binding.MovieDetailCardView.setVisibility(View.VISIBLE);
        });

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new NetworkReceiver(movieDetailActivityViewModel, currentMovieID);
        this.registerReceiver(receiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_movie_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.ShowSimilarMoviesMenuButton) {
            Intent intent = new Intent(MovieDetailActivity.this, SimilarMoviesActivity.class);
            intent.putExtra(SELECTED_MOVIE_ID, currentMovieID);
            intent.putExtra(SELECTED_MOVIE_TITLE, currentMovieTitle);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}