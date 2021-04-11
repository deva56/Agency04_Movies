package com.example.agency04movies.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MovieDetailViewModelFactory implements ViewModelProvider.Factory {

    private Application application;
    private String movieID;

    public MovieDetailViewModelFactory(Application application, String movieID) {
        this.application = application;
        this.movieID = movieID;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return modelClass.cast(new MovieDetailActivityViewModel(application, movieID));
    }
}
