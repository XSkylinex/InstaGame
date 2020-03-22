package com.example.test.contollers.database;

import android.os.Debug;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.test.contollers.Auth;
import com.example.test.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.function.Consumer;

public class UserApi {

    UserApi() {
    }

    private static CollectionReference usersCollection = FirebaseFirestore.getInstance()
            .collection("users");

    public void addUser(User user, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        usersCollection.document(user.get_id()).set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            onComplete.accept(null);
                        }else{
                            onFailure.accept(task.getException());
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }

    public void getUser(String userId, Consumer<User> onComplete, Consumer<Exception> onFailure){
        usersCollection.document(userId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user = documentSnapshot.toObject(User.class);
                        onComplete.accept(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }

    public void deleteUser(String userId, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        usersCollection.document(userId).delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onComplete.accept(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }

    public void updateUser(User user, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        usersCollection.document(user.get_id()).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                onComplete.accept(null);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });

    }

    public void getCurrentUser(Consumer<User> onComplete, Consumer<Exception> onFailure){
        getUser(Auth.getUserId(),onComplete,onFailure);
    }

}
