//Main repository for all network API calls

package com.example.agency04movies.Repository;

import com.example.agency04movies.Models.MoviesItem;
import com.example.agency04movies.Models.MoviesListItem;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.example.agency04movies.Network.RetrofitBuilder.getRetrofit;

public class NetworkCallsRepository {

    public static NetworkCallsRepository networkCallsRepository;

    public static synchronized NetworkCallsRepository getNetworkCallsRepository()
    {
        if(networkCallsRepository != null)
        {
            return networkCallsRepository;
        }
        else
        {
            return networkCallsRepository = new NetworkCallsRepository();
        }
    }

    public Observable<MoviesListItem> getPopularMovies(String api_key) {
        return getRetrofit().getPopularMovies(api_key)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }

    public Observable<MoviesListItem> getTopRatedMovies(String api_key) {
        return getRetrofit().getTopRatedMovies(api_key)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }

    public Observable<MoviesItem> getMovieDetail(String id, String api_key) {
        return getRetrofit().getMovieDetail(id, api_key)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }

    public Observable<MoviesListItem> getSimilarMovies(String id, String api_key) {
        return getRetrofit().getSimilarMovies(id, api_key)
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.io());
    }
}
