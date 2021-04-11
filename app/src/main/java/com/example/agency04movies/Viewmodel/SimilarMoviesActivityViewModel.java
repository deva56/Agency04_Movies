package com.example.agency04movies.Viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.agency04movies.Dagger.CompositeDisposableComponent;
import com.example.agency04movies.Models.MoviesListItem;
import com.example.agency04movies.MovieApplication;
import com.example.agency04movies.Repository.NetworkCallsRepository;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.example.agency04movies.Constants.api_key;

public class SimilarMoviesActivityViewModel extends AndroidViewModel {

    private final NetworkCallsRepository networkCallsRepository;
    private final CompositeDisposable compositeDisposableRxJava3;
    private MutableLiveData<Throwable> throwableLiveData;
    private MutableLiveData<MoviesListItem> moviesListItemLiveData;

    public SimilarMoviesActivityViewModel(@NonNull Application application, String similarMoviesID) {
        super(application);
        networkCallsRepository = NetworkCallsRepository.getNetworkCallsRepository();
        CompositeDisposableComponent compositeDisposableComponent = ((MovieApplication) getApplication()).getCompositeDisposableComponent();
        compositeDisposableRxJava3 = compositeDisposableComponent.getCompositeDisposable();
        getSimilarMovies(similarMoviesID, api_key);
    }

    public void getSimilarMovies(String id, String api_key) {
        compositeDisposableRxJava3.add(networkCallsRepository.getSimilarMovies(id, api_key)
                .subscribe(this::handleResponseGetSimilarMoviesFromServer, this::handleErrorGetSimilarMoviesFromServer));
    }

    private void handleResponseGetSimilarMoviesFromServer(MoviesListItem moviesListItem) {
        moviesListItemLiveData.postValue(moviesListItem);
    }

    private void handleErrorGetSimilarMoviesFromServer(Throwable throwable) {
        throwableLiveData.postValue(throwable);
    }

    public MutableLiveData<Throwable> getThrowableLiveData() {
        if (throwableLiveData == null) {
            throwableLiveData = new MutableLiveData<>();
        }
        return throwableLiveData;
    }

    public MutableLiveData<MoviesListItem> getMoviesListItemLiveData() {
        if (moviesListItemLiveData == null) {
            moviesListItemLiveData = new MutableLiveData<>();

        }
        return moviesListItemLiveData;
    }
}
