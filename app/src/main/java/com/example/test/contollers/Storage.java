package com.example.test.contollers;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.function.Consumer;

public class Storage {

    private static StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public static void uploadImage(File file,String name, Consumer<Uri> onComplete, Consumer<Exception> onFailure){
        Uri fileUri = Uri.fromFile(file);
        StorageReference reference = mStorageRef.child("images").child("posts").child(name);
        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        reference.putFile(fileUri,metadata)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(onComplete::accept).addOnFailureListener(onFailure::accept))
                .addOnFailureListener(onFailure::accept);

    }

    public static void uploadProfileImage(File file,String userId, Consumer<Uri> onComplete, Consumer<Exception> onFailure){
        Uri fileUri = Uri.fromFile(file);
        StorageReference reference = mStorageRef.child("images").child("profile_image").child(userId);
        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        reference.putFile(fileUri,metadata)
                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl()
                        .addOnSuccessListener(onComplete::accept).addOnFailureListener(onFailure::accept))
                .addOnFailureListener(onFailure::accept);

    }

}
