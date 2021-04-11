package com.example.agency04movies.Network;

import com.example.agency04movies.Models.MoviesItem;
import com.example.agency04movies.Models.MoviesListItem;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("movie/top_rated")
    Observable<MoviesListItem> getTopRatedMovies(@Query("api_key") String apiKey);

    @GET("movie/popular")
    Observable<MoviesListItem> getPopularMovies(@Query("api_key") String apiKey);

    @GET("movie/{id}")
    Observable<MoviesItem> getMovieDetail(@Path("id") String id, @Query("api_key") String apiKey );

    @GET("movie/{id}/recommendations")
    Observable<MoviesListItem> getSimilarMovies(@Path("id") String id, @Query("api_key") String apiKey );


}
