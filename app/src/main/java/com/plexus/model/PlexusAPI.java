package com.plexus.model;

import com.plexus.model.account.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface PlexusAPI {

    @GET("Users/{id}")
    Call<List<User>> getUser(@Path("id") String userId);
}
