package com.example.chatapp.fragments;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.example.chatapp.R;
import com.example.chatapp.databinding.FragmentProfileBinding;
import com.example.chatapp.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


public class ProfileFragment extends Fragment {

    public static final int CAMERA_PICK = 100; // CAMERA A
    public static final int GALLERY_PICK = 200; // GALLERY PIC
    Uri imageUri = null;
    String imageUrl, userid;
    private FragmentProfileBinding binding;
    private NavController navController;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        binding.profileImage.setOnClickListener(v -> {
            showImagePickDialog();
        });
        userid = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("users").document(userid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                UserModel userModel = task.getResult().toObject(UserModel.class);
                binding.etProfileName.setText(userModel.getUsername());
                if (userModel.getImage() != null) {
                    Glide.with(getActivity()).load(userModel.getImage()).centerCrop().into(binding.profileImage);
                }
            }
        });
        binding.profileUpdate.setOnClickListener(v -> {
            String username = binding.etProfileName.getText().toString();
            updateUsername(username);
        });
    }

    private void updateUsername(String username) {
        String currentId = firebaseAuth.getCurrentUser().getUid();
        firebaseFirestore.collection("users").document(currentId)
                .update("username", username).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (isAdded()) {
                            if (task.isSuccessful()) {
                                navController.navigate(R.id.action_profileFragment_to_userFragment);
                                Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }

    private void showImagePickDialog() {
        String[] items = {"Camera", "Gallery"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose an Option");
        builder.setItems(items, (dialogInterface, i) -> {
            if (i == 0) {
                showDialogCamera();
            }
            if (i == 1) {
                showGalleryDialogue();
            }
        });
        builder.create().show();
    }

    private void showGalleryDialogue() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        binding.progressBar2.setVisibility(View.VISIBLE);
        startActivityForResult(intent, GALLERY_PICK);

    }

    private void showDialogCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        values.put(MediaStore.Images.Media.TITLE, "Temp Desc");
        imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        binding.progressBar2.setVisibility(View.VISIBLE);
        startActivityForResult(intent, CAMERA_PICK);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            Uri uri = data.getData();

            String timestamp = String.valueOf(System.currentTimeMillis());
            String path = "Photos/" + "photos_" + timestamp;
            StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
            storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(uri1 -> {
                    String photoId = uri1.toString();
                    imageUri = uri1;
                    Glide.with(getContext()).load(imageUri).centerCrop().into(binding.profileImage);
                    binding.progressBar2.setVisibility(View.GONE);
                    //update image id
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    userid = user.getUid();
                    firebaseFirestore.collection("users").document(userid).update("image", photoId).addOnCompleteListener(t -> {

                    });

                });
            });

        }
        if (requestCode == CAMERA_PICK && resultCode == RESULT_OK) {

            Uri uri = imageUri;
            String timestamp = String.valueOf(System.currentTimeMillis());
            String path = "Photos/" + "photos_" + timestamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(path);
            storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
                Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                task.addOnSuccessListener(uri12 -> {
                    imageUri = uri12;
                    String photoId = uri12.toString();

                    Glide.with(getActivity()).load(imageUri).centerCrop().into(binding.profileImage);

                    String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    firebaseFirestore.collection("users").document(userid)
                            .update("image", photoId).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    binding.progressBar2.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "Successful added", Toast.LENGTH_SHORT).show();
                                }
                            });

                });

            });

        }
    }
}