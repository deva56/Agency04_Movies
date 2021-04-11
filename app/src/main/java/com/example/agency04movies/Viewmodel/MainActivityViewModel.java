package com.example.agency04movies.Viewmodel;

import android.app.Application;
import android.os.Bundle;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.agency04movies.Dagger.CompositeDisposableComponent;
import com.example.agency04movies.Fragments.MovieItemFragment;
import com.example.agency04movies.Models.MoviesListItem;
import com.example.agency04movies.MovieApplication;
import com.example.agency04movies.Repository.NetworkCallsRepository;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import static com.example.agency04movies.Constants.api_key;

public class MainActivityViewModel extends AndroidViewModel {

    private final NetworkCallsRepository networkCallsRepository;
    private final CompositeDisposable compositeDisposableRxJava3;
    private final List<MoviesListItem> itemList = new ArrayList<>();
    private final ArrayList<Fragment> fragments = new ArrayList<>();
    private MutableLiveData<ArrayList<Fragment>> liveDataFragments;
    private MutableLiveData<Throwable> throwableLiveData;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
        networkCallsRepository = NetworkCallsRepository.getNetworkCallsRepository();
        CompositeDisposableComponent compositeDisposableComponent = ((MovieApplication) getApplication()).getCompositeDisposableComponent();
        compositeDisposableRxJava3 = compositeDisposableComponent.getCompositeDisposable();
        getPopularMovies();
    }

    public void getPopularMovies() {
        compositeDisposableRxJava3.add(networkCallsRepository.getPopularMovies(com.example.agency04movies.Constants.api_key)
                .subscribe(this::handleResponseGetPopularFromServer, this::handleErrorGetPopularFromServer));
    }

    private void handleResponseGetPopularFromServer(MoviesListItem moviesListItem) {
        itemList.add(moviesListItem);
        compositeDisposableRxJava3.add(getTopRatedMovies(api_key)
                .subscribe(this::handleResponseGetTopRatedFromServer, this::handleErrorGetTopRatedFromServer));
    }

    private void handleErrorGetPopularFromServer(Throwable throwable) {
        throwableLiveData.postValue(throwable);
    }

    private Observable<MoviesListItem> getTopRatedMovies(String api_key) {
        return networkCallsRepository.getTopRatedMovies(api_key);
    }

    private void handleResponseGetTopRatedFromServer(MoviesListItem moviesListItem) {
        itemList.add(moviesListItem);

        for (MoviesListItem item : itemList) {
            MovieItemFragment movieItemFragment = new MovieItemFragment();
            Bundle args = new Bundle();
            args.putParcelableArrayList("MovieList", (ArrayList<? extends Parcelable>) item.getMoviesItemList());
            movieItemFragment.setArguments(args);
            fragments.add(movieItemFragment);
        }

        liveDataFragments.postValue(fragments);
    }

    private void handleErrorGetTopRatedFromServer(Throwable throwable) {
        throwableLiveData.postValue(throwable);
    }

    public LiveData<ArrayList<Fragment>> getFragments() {
        if (liveDataFragments == null) {
            liveDataFragments = new MutableLiveData<>();
        }
        return liveDataFragments;
    }

    public LiveData<Throwable> getThrowableLiveData() {
        if (throwableLiveData == null) {
            throwableLiveData = new MutableLiveData<>();
        }
        return throwableLiveData;
    }
}
