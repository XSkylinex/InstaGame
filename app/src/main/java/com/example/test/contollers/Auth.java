package com.example.test.contollers;

import com.google.firebase.auth.FirebaseAuth;

import java.util.function.Consumer;

public class Auth {

    private static FirebaseAuth auth = FirebaseAuth.getInstance();

    public static void SignUp(String email, String password, Consumer<String> onComplete, Consumer<Exception> onFailure){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String userId = getUserId();
                        onComplete.accept(userId);
                    }else{
                        onFailure.accept(task.getException());
                    }
                })
                .addOnFailureListener(onFailure::accept);

    }

    public static void SignIn(String email, String password, Consumer<String> onComplete, Consumer<Exception> onFailure){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String userId = getUserId();
                        onComplete.accept(userId);
                    }else{
                        onFailure.accept(task.getException());
                    }
                })
                .addOnFailureListener(onFailure::accept);

    }

    public static void signOut(){
        auth.signOut();
    }

    public static String getUserId(){
        return auth.getUid();
    }

    public static boolean isSignIn(){
        return getUserId() != null;
    }
}
