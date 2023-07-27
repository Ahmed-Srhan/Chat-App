package com.example.chatapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.chatapp.repository.AuthRepository;
import com.google.firebase.auth.FirebaseUser;

public class AuthViewModel extends AndroidViewModel {
    private AuthRepository repository;
    private MutableLiveData<FirebaseUser> firebaseUserMutableLiveData;

    public AuthViewModel(@NonNull Application application) {
        super(application);
        repository = new AuthRepository(application);
        firebaseUserMutableLiveData = repository.getFirebaseUserMutableLiveData();
    }

    public MutableLiveData<FirebaseUser> getFirebaseUserMutableLiveData() {
        return firebaseUserMutableLiveData;
    }

    public FirebaseUser getCurrentUser() {
        return repository.getCurrentUser();
    }


    public void sigIn(String email, String pass) {
        repository.signIn(email, pass);
    }

    public void signUp(String username, String email, String pass) {
        repository.signUp(username, email, pass);
    }

    public void signOut() {
        repository.signOut();
    }
}
