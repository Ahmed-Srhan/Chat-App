package com.example.chatapp.repository;

import androidx.annotation.Nullable;

import com.example.chatapp.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserRepository {
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private String userId;
    private List<UserModel> userModelList;
    private OnUserAvailableOnFireStore onUserAvailableOnFireStore;

    public UserRepository(OnUserAvailableOnFireStore onUserAvailableOnFireStore) {
        this.onUserAvailableOnFireStore = onUserAvailableOnFireStore;
        userModelList = new ArrayList<>();

    }

    public void getUserInFirebaseFireStore() {
        userId = Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        firebaseFirestore.collection("users").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                userModelList.clear();
                assert value != null;
                for (DocumentSnapshot ds : value.getDocuments()) {
                    UserModel userModel = ds.toObject(UserModel.class);
                    assert userModel != null;
                    if (!userId.equals(userModel.getUserId())) {
                        userModelList.add(userModel);
                        onUserAvailableOnFireStore.onUserAvailable(userModelList);
                    }
                }
            }
        });
    }

    public interface OnUserAvailableOnFireStore {
        void onUserAvailable(List<UserModel> userModelList);
    }
}
