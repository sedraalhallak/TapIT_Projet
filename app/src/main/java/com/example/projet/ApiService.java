package com.example.projet;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface ApiService {

    //Get all the songs
    @GET("api/songs")
    Call<List<Song>> getSongs();

}
