package com.example.agency04movies.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.agency04movies.Dagger.CompositeDisposableComponent;
import com.example.agency04movies.Models.MoviesItem;
import com.example.agency04movies.MovieApplication;
import com.example.agency04movies.Repository.NetworkCallsRepository;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class MovieDetailActivityViewModel extends AndroidViewModel {

    private final NetworkCallsRepository networkCallsRepository;
    private final CompositeDisposable compositeDisposableRxJava3;
    private MutableLiveData<Throwable> throwableLiveData;
    private MutableLiveData<MoviesItem> moviesItemLiveData;

    public MovieDetailActivityViewModel(@NonNull Application application, String movieID) {
        super(application);
        networkCallsRepository = NetworkCallsRepository.getNetworkCallsRepository();
        CompositeDisposableComponent compositeDisposableComponent = ((MovieApplication) getApplication()).getCompositeDisposableComponent();
        compositeDisposableRxJava3 = compositeDisposableComponent.getCompositeDisposable();
        getMovieDetailFromNetwork(movieID);
    }

    public void getMovieDetailFromNetwork(String id) {
        compositeDisposableRxJava3.add(networkCallsRepository.getMovieDetail(id, com.example.agency04movies.Constants.api_key)
                .subscribe(this::handleResponseGetMovieDetailFromServer, this::handleErrorGetMovieDetailFromServer));
    }

    private void handleResponseGetMovieDetailFromServer(MoviesItem moviesItem) {
        moviesItemLiveData.postValue(moviesItem);
    }

    private void handleErrorGetMovieDetailFromServer(Throwable throwable) {
        throwableLiveData.postValue(throwable);
    }

    public LiveData<Throwable> getThrowableLiveData() {
        if (throwableLiveData == null) {
            throwableLiveData = new MutableLiveData<>();
        }
        return throwableLiveData;
    }

    public LiveData<MoviesItem> getMoviesItemLiveData() {
        if (moviesItemLiveData == null) {
            moviesItemLiveData = new MutableLiveData<>();
        }
        return moviesItemLiveData;
    }
}
