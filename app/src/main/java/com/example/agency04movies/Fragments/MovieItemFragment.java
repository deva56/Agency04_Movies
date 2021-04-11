/*
Fragment which contains recyclerview and all results that are fetched from network call. This fragment is later put inside viewpager.
 */

package com.example.agency04movies.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.agency04movies.Activity.MovieDetailActivity;
import com.example.agency04movies.Models.MoviesItem;
import com.example.agency04movies.R;
import com.example.agency04movies.RecyclerViewAdapter.FragmentMovieListAdapter;

import java.util.List;

import static com.example.agency04movies.Constants.SELECTED_MOVIE_ID;
import static com.example.agency04movies.Constants.SELECTED_MOVIE_TITLE;

public class MovieItemFragment extends Fragment {

    private List<MoviesItem> list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        assert getArguments() != null;
        list = getArguments().getParcelableArrayList("MovieList");
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.movie_list_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.MovieListFragmentRecyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        recyclerView.setHasFixedSize(true);

        FragmentMovieListAdapter adapter = new FragmentMovieListAdapter(list, getActivity().getApplicationContext());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(moviesItem -> {
            Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
            intent.putExtra(SELECTED_MOVIE_ID, moviesItem.getId());
            intent.putExtra(SELECTED_MOVIE_TITLE, moviesItem.getTitle());
            startActivity(intent);
        });
    }
}
