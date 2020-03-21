package com.example.test.contollers;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.function.Consumer;

public class Auth {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static void SignUp(String email, String password, Consumer<String> onComplete, Consumer<Exception> onFailure){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String userId = getUserId();
                            onComplete.accept(userId);
                        }else{
                            onFailure.accept(task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailure.accept(e);
                    }
                });

    }

    public static String getUserId(){
        return auth.getUid();
    }
}
