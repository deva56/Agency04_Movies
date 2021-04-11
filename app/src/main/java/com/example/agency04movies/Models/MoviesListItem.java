package com.example.agency04movies.Models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MoviesListItem {

    @SerializedName("results")
    private List<MoviesItem> moviesItemList;

    public List<MoviesItem> getMoviesItemList() {
        return moviesItemList;
    }
}
