package com.example.chatapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.chatapp.R;
import com.example.chatapp.databinding.FragmentRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;


public class RegisterFragment extends Fragment {
    private HashMap<String, Object> userHashMap = new HashMap<>();
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    private FragmentRegisterBinding binding;
    private NavController navController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        binding.progressBar.setVisibility(View.GONE);
        binding.signUpBtn.setOnClickListener(v -> {
            String username = binding.etNameSignUp.getText().toString();
            String email = binding.etEmailSignUp.getText().toString();
            String pass = binding.etPasswordSignUp.getText().toString();

            if (username.isEmpty() || email.isEmpty() || pass.isEmpty()) {

                Toast.makeText(getContext(), "Fields are required", Toast.LENGTH_SHORT).show();
            }
            if (username.isEmpty()) {

                binding.etNameSignUp.setError("Enter a name");
            }
            if (pass.length() < 6) {
                binding.etPasswordSignUp.setError("Password Length Must Be 6 or more Chars");
            }
            if (email.isEmpty()) {
                binding.etEmailSignUp.setError("Enter email Bitch");
            }
            if (!username.isEmpty() && !email.isEmpty() && !pass.isEmpty()) {
                binding.progressBar.setVisibility(View.VISIBLE);
                signUp(username, email, pass);
            }


        });
        binding.goToSignInActivity.setOnClickListener(v -> {

            navController.navigate(R.id.action_registerFragment_to_loginFragment);
        });
    }

    public void signUp(String username, String email, String pass) {
        firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String userId = firebaseAuth.getCurrentUser().getUid();
                    userHashMap.put("username", username);
                    userHashMap.put("userId", userId);
                    userHashMap.put("image", "default");
                    userHashMap.put("status", "offline");
                    firebaseFirestore.collection("users").document(userId).set(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {

                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (!task.isSuccessful())
                                Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    navController.navigate(R.id.action_registerFragment_to_loginFragment);
                    binding.progressBar.setVisibility(View.GONE);

                } else {
                    Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    binding.progressBar.setVisibility(View.GONE);


                }
            }
        });

    }
}