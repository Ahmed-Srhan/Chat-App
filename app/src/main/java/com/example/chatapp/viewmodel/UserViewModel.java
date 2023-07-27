package com.example.chatapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatapp.model.UserModel;
import com.example.chatapp.repository.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel implements UserRepository.OnUserAvailableOnFireStore {
    private UserRepository repository;
    private MutableLiveData<List<UserModel>> listMutableLiveData;

    public UserViewModel() {
        listMutableLiveData = new MutableLiveData<>();
        repository = new UserRepository(this);
        repository.getUserInFirebaseFireStore();

    }

    public MutableLiveData<List<UserModel>> getListMutableLiveData() {
        return listMutableLiveData;
    }

    @Override
    public void onUserAvailable(List<UserModel> userModelList) {
        listMutableLiveData.setValue(userModelList);
    }
}
