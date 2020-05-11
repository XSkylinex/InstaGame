package com.example.test.contollers;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Consumer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static androidx.constraintlayout.widget.Constraints.TAG;

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

    public static void updatePassword(String userMail, String oldPassword, String newPassword, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

       // Get auth credentials from the user for re-authentication. The example below shows
       // email and password credentials but there are multiple possible providers,
       // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider.getCredential(userMail, oldPassword);
        assert user != null;
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Password updated");
                                        onComplete.accept(null);
                                    } else {
                                        Log.d(TAG, "Error password not updated");
                                        onFailure.accept(task.getException());
                                    }
                                }
                            });
                        } else {
                            Log.d(TAG, "Error auth failed");
                            onFailure.accept(task.getException());
                        }
                    }
                });



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
