package com.example.chatapp.repository;

import androidx.annotation.Nullable;

import com.example.chatapp.model.MessageModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MessageRepository {
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String userId;
    private List<MessageModel> messageModelList;
    private OnMessageAdded onMessageAdded;

    public MessageRepository(OnMessageAdded onMessageAdded) {
        this.onMessageAdded = onMessageAdded;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        messageModelList = new ArrayList<>();
    }

    public void getAllMessageFromFireStore(String friendId) {
        userId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("message").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                messageModelList.clear();
                for (DocumentSnapshot ds : value.getDocuments()) {
                    MessageModel messageModel = ds.toObject(MessageModel.class);
                    if (userId.equals(messageModel.getSender()) && friendId.equals(messageModel.getReceiver()) ||
                            userId.equals(messageModel.getReceiver()) && friendId.equals(messageModel.getSender())) {
                        messageModelList.add(messageModel);
                        onMessageAdded.messageFromFireStore(messageModelList);
                    }

                }
            }
        });
    }

    public interface OnMessageAdded {
        void messageFromFireStore(List<MessageModel> messageModelList);
    }


}
