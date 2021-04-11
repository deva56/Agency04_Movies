package com.example.agency04movies;

import android.app.Application;

import com.example.agency04movies.Dagger.CompositeDisposableComponent;
import com.example.agency04movies.Dagger.DaggerCompositeDisposableComponent;

public class MovieApplication extends Application {
    private CompositeDisposableComponent compositeDisposableComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        compositeDisposableComponent = DaggerCompositeDisposableComponent.create();
    }

    public CompositeDisposableComponent getCompositeDisposableComponent() {
        return compositeDisposableComponent;
    }
}
