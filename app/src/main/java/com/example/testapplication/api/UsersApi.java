package com.example.testapplication.api;

import com.example.Login;
import com.example.testapplication.model.userDelete.UserDelete;
import com.example.testapplication.model.userDetails.SingleUser;
import com.example.testapplication.model.userList.UserList;

import java.util.Map;

import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UsersApi {
    @POST("login")
    Single<Login> getLogin(
            @Query("email") String name,
            @Query("password") String password
    );

    @GET("user/{id}")
    Single<SingleUser> getUserDetails(
            @Path("id") String id
    );

    @GET("user/delete/{id}")
    Single<UserDelete> getUserDelete(
            @Path("id") String id
    );

    @GET("user/index")
    Single<UserList> getUserList(
    );
}
