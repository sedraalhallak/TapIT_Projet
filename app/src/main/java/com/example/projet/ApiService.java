package com.example.projet;
import retrofit2.Call;
import retrofit2.http.GET;

import java.util.List;

public interface ApiService {

    @GET("/songs")
    Call<List<Song>> getSongs();
}
