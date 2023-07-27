package com.example.chatapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.adapter.UserAdapter;
import com.example.chatapp.databinding.FragmentUserBinding;
import com.example.chatapp.model.UserModel;
import com.example.chatapp.viewmodel.UserViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.List;


public class UserFragment extends Fragment implements UserAdapter.OnUserClickListener {
    private FragmentUserBinding binding;
    private NavController navController;
    private UserViewModel viewModel;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private UserAdapter adapter;
    private String token;
    private String userIdForToken;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        adapter = new UserAdapter(this, getContext());
        viewModel = new ViewModelProvider(this).get(UserViewModel.class);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerViewUser.setHasFixedSize(true);
        binding.recyclerViewUser.setLayoutManager(new LinearLayoutManager(getContext()));
        viewModel.getListMutableLiveData().observe(getViewLifecycleOwner(), userModelList -> {
            adapter.setUserModelList(userModelList);
            binding.recyclerViewUser.setAdapter(adapter);
            binding.progressBar.setVisibility(View.GONE);
        });
        binding.logoutButton.setOnClickListener(v -> {
            if (firebaseAuth.getCurrentUser() != null) {
                firebaseAuth.signOut();
                String id = firebaseAuth.getCurrentUser().getUid();
                firebaseFirestore.collection("users").document(id)
                        .update("status", "offline").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });


                navController.navigate(R.id.action_userFragment_to_loginFragment);
            }
        });

        String userId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("users").document(userId)
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        UserModel userModel = task.getResult().toObject(UserModel.class);
                        assert userModel != null;
                        binding.tvUserNameOnUserFragment.setText(userModel.getUsername());
                        if (userModel.getImage() != null) {
                            Glide.with(getActivity()).load(userModel.getImage()).into(binding.imageViewUser);
                        }
                    }
                });

        binding.imageViewUser.setOnClickListener(v -> {
            navController.navigate(R.id.action_userFragment_to_profileFragment);
        });
    }

    private void setStatus(String status) {
        String id = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("users").document(id)
                .update("status", status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });


        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                token = instanceIdResult.getToken();
                GenerateToken(token);

            }
        });

    }

    private void GenerateToken(String token) {

        userIdForToken = firebaseAuth.getCurrentUser().getUid();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("token", token);

        firebaseFirestore.collection("Tokens").document(userIdForToken).set(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }


    @Override
    public void UserClicked(int position, List<UserModel> userModelList) {
        UserFragmentDirections.ActionUserFragmentToChatFragment action =
                UserFragmentDirections.actionUserFragmentToChatFragment();

        action.setPosition(position);
        action.setImageUrl(userModelList.get(position).getImage());
        action.setFriendid(userModelList.get(position).getUserId());
        action.setUsername(userModelList.get(position).getUsername());
        navController.navigate(action);

    }


    @Override
    public void onResume() {
        super.onResume();
        setStatus("online");
    }


    @Override
    public void onPause() {
        super.onPause();
        setStatus("offline");
    }
}







