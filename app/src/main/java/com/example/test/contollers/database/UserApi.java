package com.example.test.contollers.database;

import androidx.annotation.NonNull;

import com.example.test.contollers.Auth;
import com.example.test.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if (Objects.requireNonNull(document).exists()) {
                        User user = document.toObject(User.class);
                        onComplete.accept(user);
                    } else {
                        onFailure.accept(new NullPointerException("user not Exist"));
                    }
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

    public void getCurrentUser(Consumer<User> onComplete, Consumer<Exception> onFailure){
        getUser(Auth.getUserId(),onComplete,onFailure);
    }

}
