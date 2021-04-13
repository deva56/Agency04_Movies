/*
Activity which lists out similar movies if there are any in a recyclerview from previously selected movie.
From there user can select another movie and find similar movie for that movie and repeat the process as long as there are similar
movies from selection. Contains button to revert all the way to main screen and erases the back stack.
 */

package com.example.agency04movies.Activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agency04movies.BroadcastReceivers.SimilarMoviesNetworkReceiver;
import com.example.agency04movies.R;
import com.example.agency04movies.RecyclerViewAdapter.FragmentMovieListAdapter;
import com.example.agency04movies.Viewmodel.SimilarMoviesActivityViewModel;
import com.example.agency04movies.Viewmodel.SimilarMoviesViewModelFactory;
import com.example.agency04movies.databinding.ActivitySimilarMoviesBinding;

import static com.example.agency04movies.Constants.SELECTED_MOVIE_ID;
import static com.example.agency04movies.Constants.SELECTED_MOVIE_TITLE;
import static com.example.agency04movies.Constants.TAG;

public class SimilarMoviesActivity extends AppCompatActivity {

    private ActivitySimilarMoviesBinding binding;
    private RecyclerView recyclerView;
    private SimilarMoviesNetworkReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySimilarMoviesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Toolbar toolbar = binding.SimilarMoviesAppBar;
        this.setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = binding.SimilarMoviesRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);

        String similarMovieID = getIntent().getStringExtra(SELECTED_MOVIE_ID);
        String similarMovieTitle = getIntent().getStringExtra(SELECTED_MOVIE_TITLE);

        binding.SimilarMoviesAppBarTextView.setText(getString(R.string.similarMovies) + " " + similarMovieTitle);
        binding.SimilarMoviesProgressBar.setVisibility(View.VISIBLE);

        SimilarMoviesActivityViewModel similarMoviesActivityViewModel
                = new ViewModelProvider(this, new SimilarMoviesViewModelFactory(getApplication(), similarMovieID))
                .get(SimilarMoviesActivityViewModel.class);

        similarMoviesActivityViewModel.getThrowableLiveData().observe(this, throwable -> {
            receiver.setLockVariable(true);
            binding.SimilarMoviesProgressBar.setVisibility(View.GONE);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.errorDescription));
            builder.setTitle(getString(R.string.errorTitle));
            builder.setPositiveButton(getString(R.string.dismiss), (dialogInterface, i) -> dialogInterface.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
            Log.d(TAG, "handleErrorGetMovieDetailFromServer: " + throwable.getMessage());
        });

        similarMoviesActivityViewModel.getMoviesListItemLiveData().observe(this, moviesListItem -> {
            receiver.setMoviesListItem(moviesListItem);
            receiver.setLockVariable(true);

            binding.SimilarMoviesProgressBar.setVisibility(View.GONE);

            if (moviesListItem.getMoviesItemList().size() != 0) {
                binding.SimilarMoviesTextView.setVisibility(View.GONE);
                FragmentMovieListAdapter adapter = new FragmentMovieListAdapter(moviesListItem.getMoviesItemList(), getApplicationContext());
                recyclerView.setAdapter(adapter);

                adapter.setOnItemClickListener(moviesItem -> {
                    Intent intent = new Intent(getApplicationContext(), MovieDetailActivity.class);
                    intent.putExtra(SELECTED_MOVIE_ID, moviesItem.getId());
                    intent.putExtra(SELECTED_MOVIE_TITLE, moviesItem.getTitle());
                    startActivity(intent);
                });
            } else {
                binding.SimilarMoviesTextView.setVisibility(View.VISIBLE);
            }
        });

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        receiver = new SimilarMoviesNetworkReceiver(similarMoviesActivityViewModel, similarMovieID, this);
        this.registerReceiver(receiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.activity_similar_movies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (item.getItemId() == R.id.BackToMainScreenMenuButton) {
            Intent intent = new Intent(SimilarMoviesActivity.this, MainActivity.class);
            startActivity(intent);
            finishAffinity();
            finish();
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