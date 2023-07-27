package com.example.chatapp.fragments;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.adapter.MessageAdapter;
import com.example.chatapp.databinding.FragmentChatBinding;
import com.example.chatapp.model.MessageModel;
import com.example.chatapp.model.UserModel;
import com.example.chatapp.notification.ApiServiceShit;
import com.example.chatapp.notification.Data;
import com.example.chatapp.notification.Response;
import com.example.chatapp.notification.Sender;
import com.example.chatapp.notification.Token;
import com.example.chatapp.viewmodel.MessageViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;

public class ChatFragment extends Fragment {
    boolean notify = false;
    String nameofsender;
    private FragmentChatBinding binding;
    private NavController navController;
    private MessageViewModel viewModel;
    private MessageAdapter adapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String username, imageUrl, friendId, userId, message, token, useridfortoken;
    private int position;
    private ApiServiceShit apiserviceshit;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        viewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        adapter = new MessageAdapter();
        binding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        binding.recyclerView.setLayoutManager(layoutManager);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        userId = firebaseAuth.getCurrentUser().getUid();
        position = ChatFragmentArgs.fromBundle(getArguments()).getPosition();
        username = ChatFragmentArgs.fromBundle(getArguments()).getUsername();
        imageUrl = ChatFragmentArgs.fromBundle(getArguments()).getImageUrl();
        friendId = ChatFragmentArgs.fromBundle(getArguments()).getFriendid();


        binding.chatFragUserName.setText(username);

        viewModel.getMessageFromFireStore(friendId);
        viewModel.getMessageModelMutableLiveData().observe(getViewLifecycleOwner(), new Observer<List<MessageModel>>() {
            @Override
            public void onChanged(List<MessageModel> messageModelList) {
                adapter.setMessageModelList(messageModelList);
                binding.recyclerView.setAdapter(adapter);
            }
        });

        binding.chatBackButton.setOnClickListener(v -> {
            navController.navigate(R.id.action_chatFragment_to_userFragment);

        });

        binding.sendMessage.setOnClickListener(v -> {
            message = binding.etMessage.getText().toString();
            if (message.isEmpty()) {
                binding.etMessage.setError("Type something Bitch");
            } else {
                sendMessage(message, userId, friendId);
                binding.etMessage.setText("");
            }
        });


        firebaseFirestore.collection("users").document(friendId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel userModel = task.getResult().toObject(UserModel.class);
                Glide.with(getContext()).load(userModel.getImage()).into(binding.chatImageUser);
            }

        });

    }

    private void sendMessage(String message, String userId, String friendId) {


        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        String currentTime = formatter.format(date);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", userId);
        hashMap.put("receiver", friendId);
        hashMap.put("message", message);
        hashMap.put("time", currentTime);

        firebaseFirestore.collection("message").document(currentTime)
                .set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });

        firebaseFirestore.collection("Users").document(userId).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                UserModel userModel = documentSnapshot.toObject(UserModel.class);
                nameofsender = userModel.getUsername();

                Log.d(TAG, "onSuccess: " + nameofsender);


                if (notify) {

                    SendNotification(friendId, nameofsender, message);
                    Log.d(TAG, "notify: " + nameofsender);


                }

                notify = false;


            }
        });


    }

    private void SendNotification(String friendid, String nameofsender, String message) {


        useridfortoken = firebaseAuth.getCurrentUser().getUid();


        firebaseFirestore.collection("Tokens").document(friendid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {


                assert value != null;
                Token objectotoken = value.toObject(Token.class);
                assert objectotoken != null;
                token = objectotoken.getToken();

                Log.d(TAG, "onEvent: " + token);


                Data data = new Data(useridfortoken, R.mipmap.ic_launcher, message, "New Message From " + nameofsender, friendid);

                Sender sender = new Sender(data, token);
                apiserviceshit.sendNotification(sender).enqueue(new Callback<Response>() {
                    @Override
                    public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                        if (response.code() == 200) {

                            if (response.body().success != 1) {

                                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();

                            }


                        }
                    }

                    @Override
                    public void onFailure(Call<Response> call, Throwable t) {

                    }
                });


            }
        });

    }

    @Override
    public void onStop() {
        super.onStop();
        viewModel.resetAll();
    }
}