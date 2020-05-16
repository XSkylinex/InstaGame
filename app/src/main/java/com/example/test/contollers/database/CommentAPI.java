package com.example.test.contollers.database;

import androidx.annotation.Nullable;

import com.example.test.models.Comment;
import com.example.test.models.listener.Listener;
import com.example.test.models.listener.ListenerFirebaseAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class CommentAPI {

    static final String Comments = "comments";

    CommentAPI(){}




    static final FirebaseFirestore instance = FirebaseFirestore.getInstance();


    private static CollectionReference getCollection(String postId){
        return instance.collection(PostAPI.POSTS).document(postId).collection(Comments);
    }

    private static DocumentReference getDoc(String userId, String notificationId){
        return getCollection(userId).document(notificationId);
    }
    private static DocumentReference getDoc(Comment comment){
        return getDoc(comment.get_postId(),comment.get_id());
    }

    public String generateCommentId(String postId){
        return getCollection(postId).document().getId();
    }

    public void addComment(Comment comment, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        getDoc(comment).set(comment)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onComplete.accept(null);
                    }else{
                        onFailure.accept(task.getException());
                    }
                }).addOnFailureListener(onFailure::accept);
    }

    public void getComment(String postId, String commentId, Consumer<Comment> onComplete, Consumer<Exception> onFailure){
        getDoc(postId,commentId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Comment comment = documentSnapshot.toObject(Comment.class);
                    onComplete.accept(comment);
                })
                .addOnFailureListener(onFailure::accept);
    }


    public Listener listenComment(String postId,String commentId, Consumer<Comment> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration = getDoc(postId,commentId).addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                onFailure.accept(e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                Comment comment = snapshot.toObject(Comment.class);
                onComplete.accept(comment);
            } else {
                onComplete.accept(null);
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenComments(String postId,Consumer<List<Comment>> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration = getCollection(postId).addSnapshotListener((queryDocumentSnapshots, e) -> {
            if (e != null) {
                onFailure.accept(e);
                return;
            }
            assert queryDocumentSnapshots != null;
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
            List<Comment> comments = new ArrayList<>();
            for (DocumentSnapshot doc : documents) {
                comments.add(doc.toObject(Comment.class));
            }
            onComplete.accept(comments);
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenCommentsChanges(String postId,Consumer<Comment> onAdded, Consumer<Comment> onModified, Consumer<Comment> onRemoved, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration = getCollection(postId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    onFailure.accept(e);
                    return;
                }
                assert queryDocumentSnapshots != null;
                List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                for (DocumentChange documentChange : documentChanges) {
                    Comment comment = documentChange.getDocument().toObject(Comment.class);
                    switch (documentChange.getType()) {
                        case ADDED:
                            onAdded.accept(comment);
                            break;
                        case MODIFIED:
                            onModified.accept(comment);
                            break;
                        case REMOVED:
                            onRemoved.accept(comment);
                            break;
                    }
                }
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }



    public void getComments(String postId,Consumer<List<Comment>> onComplete, Consumer<Exception> onFailure) {
        getCollection(postId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
            List<Comment> comments = new ArrayList<>();
            for (DocumentSnapshot document : documents) {
                comments.add(document.toObject(Comment.class));
            }
            onComplete.accept(comments);
        }).addOnFailureListener(onFailure::accept);
    }

    public void deleteComment(String postId,String commentId, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        getCollection(postId).document(commentId).delete()
                .addOnSuccessListener(aVoid -> onComplete.accept(null)).addOnFailureListener(onFailure::accept);
    }

    public void updateComment(Comment comment, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        getCollection(comment.get_postId()).document(comment.get_id()).set(comment)
                .addOnSuccessListener(aVoid -> onComplete.accept(null)).addOnFailureListener(onFailure::accept);

    }

    public Listener listenCommentsFromPost(String postId, Consumer<List<Comment>> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration =
                getCollection(postId) // .whereEqualTo("_postId",postId)
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                onFailure.accept(e);
                                return;
                            }
                            assert queryDocumentSnapshots != null;
                            List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                            List<Comment> comments = new ArrayList<>();
                            for (DocumentSnapshot doc : documents) {
                                comments.add(doc.toObject(Comment.class));
                            }
                            onComplete.accept(comments);
                        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenCommentsChangesFromPost(String postId ,Runnable onStart,
                                                  Consumer<Comment> onAdded,
                                                  Consumer<Comment> onModified,
                                                  Consumer<Comment> onRemoved,
                                                  Runnable onComplete,
                                                  Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration =
                getCollection(postId) //.whereEqualTo("_postId",postId)
                        .addSnapshotListener((queryDocumentSnapshots, e) -> {
                            if (e != null) {
                                onFailure.accept(e);
                                return;
                            }
                            assert queryDocumentSnapshots != null;
                            List<DocumentChange> documentChanges = queryDocumentSnapshots.getDocumentChanges();
                            onStart.run();
                            for (DocumentChange documentChange : documentChanges) {
                                Comment comment = documentChange.getDocument().toObject(Comment.class);
                                switch (documentChange.getType()) {
                                    case ADDED:
                                        onAdded.accept(comment);
                                        break;
                                    case MODIFIED:
                                        onModified.accept(comment);
                                        break;
                                    case REMOVED:
                                        onRemoved.accept(comment);
                                        break;
                                }
                            }
                            onComplete.run();
                        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }



    public void getCommentsFromPost(String postId,Consumer<List<Comment>> onComplete, Consumer<Exception> onFailure) {
        getCollection(postId) //.whereEqualTo("_postId",postId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<Comment> comments = new ArrayList<>();
                for (DocumentSnapshot document : documents) {
                    comments.add(document.toObject(Comment.class));
                }
                onComplete.accept(comments);
            }
        }).addOnFailureListener(onFailure::accept);
    }



}
