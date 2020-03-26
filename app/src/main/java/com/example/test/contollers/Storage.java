package com.example.test.contollers;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.function.Consumer;

public class Storage {

    private static StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();

    public static void uploadImage(File file,String name, Consumer<Uri> onComplete, Consumer<Exception> onFailure){
        Uri fileUri = Uri.fromFile(file);
        StorageReference reference = mStorageRef.child("images").child(name);
        // Create file metadata including the content type
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("image/jpg")
                .build();
        reference.putFile(fileUri,metadata)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                onComplete.accept(uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                onFailure.accept(e);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailure.accept(e);
                    }
                });

    }
}
