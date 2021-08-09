package com.example.testapplication.api;


import com.example.Login;
import com.example.testapplication.model.userDelete.UserDelete;
import com.example.testapplication.model.userDetails.SingleUser;
import com.example.testapplication.model.userList.UserList;

import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkService {

    private static final String BASE_URL = "https://api.dev.trapme.io/test-api/public/api/";

    private static NetworkService instance;

    private UsersApi api = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build()
            .create(UsersApi.class);


    private NetworkService() {
    }

    public static NetworkService getInstance() {
        if (instance == null) {
            instance = new NetworkService();
        }
        return instance;
    }

    public Single<Login> getLogin(String email, String password){
        return api.getLogin(email, password);
    }

    public Single<SingleUser> getUserDetails(String id){
        return api.getUserDetails(id);
    }
    public Single<UserDelete> getUserDelete(String id){
        return api.getUserDelete(id);
    }

    public Single<UserList> getUserList(){
        return api.getUserList();
    }
}
