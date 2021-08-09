package com.example.testapplication.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.example.Login;
import com.example.testapplication.api.NetworkService;
import com.example.testapplication.model.userDelete.UserDelete;
import com.example.testapplication.model.userDetails.SingleUser;
import com.example.testapplication.model.userList.UserList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.observers.DisposableSingleObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ListViewModel extends ViewModel {


    /**
     * only exposes immutable UserList LiveData objects to observe users
     */
    public MutableLiveData<Login> usersLogin = new MutableLiveData<Login>();
    public MutableLiveData<SingleUser> userDetails = new MutableLiveData<>();
    public MutableLiveData<UserList> userList = new MutableLiveData<>();
    public MutableLiveData<UserDelete> userDelete = new MutableLiveData<>();

    /**
     * only exposes immutable Boolen LiveData objects to observe usersLoadError
     */
    public MutableLiveData<Boolean> usersLoadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> usersDetailsLoadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> userListLoadError = new MutableLiveData<Boolean>();
    public MutableLiveData<Boolean> userDeleteLoadError = new MutableLiveData<Boolean>();

    /**
     * only exposes immutable Boolen LiveData objects to observe loading
     */
    public MutableLiveData<Boolean> loading = new MutableLiveData<Boolean>();

    /**
     * Call network service
     */
    private NetworkService networkService = NetworkService.getInstance();

    private CompositeDisposable disposable = new CompositeDisposable();


    public void login(String email, String password) {
        disposable.add(
                networkService.getLogin(email, password)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<Login>() {
                            @Override
                            public void onSuccess(@NonNull Login login) {
                                usersLogin.setValue(login);
                                usersLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                usersLoadError.setValue(true);
                            }
                        })

        );
    }

    public void getUserDetails(String id) {
        disposable.add(
                networkService.getUserDetails(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<SingleUser>() {
                            @Override
                            public void onSuccess(@NonNull SingleUser singleUser) {
                                userDetails.setValue(singleUser);
                                usersDetailsLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                usersDetailsLoadError.setValue(true);
                            }
                        })
        );

    }

    public void getUserDelete(String id) {
        disposable.add(
                networkService.getUserDelete(id)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserDelete>() {
                            @Override
                            public void onSuccess(@NonNull UserDelete delete) {
                                userDelete.setValue(delete);
                                userDeleteLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                userDeleteLoadError.setValue(true);
                            }
                        })
        );

    }


    public void getUserList() {
        disposable.add(
                networkService.getUserList()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<UserList>() {
                            @Override
                            public void onSuccess(@NonNull UserList list) {
                                userList.setValue(list);
                                userListLoadError.setValue(false);
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                userListLoadError.setValue(true);
                            }
                        })
        );

    }


    /**
     * Using clear CompositeDisposable, but can accept new disposable
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }
}
