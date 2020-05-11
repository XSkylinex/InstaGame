package com.example.test.contollers.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Consumer;

import com.example.test.models.Notification;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class NotificationAPI {

    static final String NOTIFICATION = "notifications";
    NotificationAPI(){}

    static final FirebaseFirestore instance = FirebaseFirestore.getInstance();


    private static CollectionReference getCollection(String userId){
        return instance.collection(UserApi.USERS).document(userId).collection(NOTIFICATION);
    }

    private static DocumentReference getDoc(String userId, String notificationId){
        return getCollection(userId).document(notificationId);
    }
    private static DocumentReference getDoc(Notification notification){
        return getDoc(notification.get_user_id(),notification.get_id());
    }

    public String generateNotificationId(String userId){

        return getCollection(userId).document().getId();
    }

    public void addNotification(Notification notification, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        getDoc(notification).set(notification)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onComplete.accept(null);
                    }else{
                        onFailure.accept(task.getException());
                    }
                }).addOnFailureListener(onFailure::accept);
    }

    public void getNotifications(String userId,Consumer<List<Notification>> onComplete, Consumer<Exception> onFailure){
        getCollection(userId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final List<DocumentSnapshot> documents = task.getResult().getDocuments();
                ArrayList<Notification> notifications=new ArrayList<>();
                for (DocumentSnapshot document : documents) {
                    notifications.add(document.toObject(Notification.class));
                }
                onComplete.accept(notifications);
            }else {
                onFailure.accept(task.getException());
            }
        });
    }
    public void listenNotifications(String userId,Consumer<Notification> onAdded, Consumer<Notification> onModified,
                                    Consumer<Notification> onRemoved, Consumer<Exception> onFailure){
        getCollection(userId).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null){
                onFailure.accept(e);
                return;
            }else {
                final List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                for (DocumentChange documentChange : documentChanges) {
                    final Notification notification = documentChange.getDocument().toObject(Notification.class);
                    switch (documentChange.getType()){
                        case ADDED:{
                            onAdded.accept(notification);
                            break;
                        }
                        case MODIFIED:{
                            onModified.accept(notification);
                            break;
                        }
                        case REMOVED:{
                            onRemoved.accept(notification);
                            break;
                        }
                    }
                }
            }
        });
    }

    public void getNotification(String userId, String notificationId, Consumer<Notification> onComplete, Consumer<Exception> onFailure){
        getDoc(userId,notificationId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Notification notification = documentSnapshot.toObject(Notification.class);
                    onComplete.accept(notification);
                })
                .addOnFailureListener(onFailure::accept);
    }

    public void deleteNotifications(@NonNull String userId,@NonNull String type,@NonNull String creator, @Nullable String post_id, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        Query query = getCollection(userId).whereEqualTo("_type", type).whereEqualTo("_creator", creator);
        if (post_id != null)
            query = query.whereEqualTo("_post_id",post_id);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    final QuerySnapshot result = task.getResult();
                    assert result != null;
                    final List<DocumentSnapshot> documents = result.getDocuments();
                    // Get a new write batch
                    WriteBatch batch = FirebaseFirestore.getInstance().batch();
                    for (DocumentSnapshot document : documents) {
                        batch.delete(getDoc(userId,document.getId()));
                    }
                    batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                onComplete.accept(task.getResult());
                            }else {
                                onFailure.accept(task.getException());
                            }
                        }
                    });
                }else {
                    onFailure.accept(task.getException());
                }
            }
        });
    }
    public void deleteNotifications(@NonNull String userId,@NonNull String post_id, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        Query query = getCollection(userId).whereEqualTo("_post_id",post_id);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                final QuerySnapshot result = task.getResult();
                assert result != null;
                final List<DocumentSnapshot> documents = result.getDocuments();
                // Get a new write batch
                WriteBatch batch = FirebaseFirestore.getInstance().batch();
                for (DocumentSnapshot document : documents) {
                    batch.delete(getDoc(userId,document.getId()));
                }
                batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            onComplete.accept(task.getResult());
                        }else {
                            onFailure.accept(task.getException());
                        }
                    }
                });
            }else {
                onFailure.accept(task.getException());
            }
        });
    }

}
