package com.example.chatapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chatapp.model.MessageModel;
import com.example.chatapp.repository.MessageRepository;

import java.util.List;

public class MessageViewModel extends ViewModel implements MessageRepository.OnMessageAdded {
    private MutableLiveData<List<MessageModel>> messageModelMutableLiveData;
    private MessageRepository repository;

    public MessageViewModel() {
        repository = new MessageRepository(this);
        messageModelMutableLiveData = new MutableLiveData<>();

    }

    public MutableLiveData<List<MessageModel>> getMessageModelMutableLiveData() {
        return messageModelMutableLiveData;
    }

    public void getMessageFromFireStore(String friendId) {
        repository.getAllMessageFromFireStore(friendId);
    }

    public void resetAll() {
        messageModelMutableLiveData.setValue(null);
    }

    @Override
    public void messageFromFireStore(List<MessageModel> messageModelList) {
        messageModelMutableLiveData.setValue(messageModelList);
    }
}
