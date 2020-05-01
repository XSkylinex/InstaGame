package com.example.test.contollers.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.test.models.Comment;
import com.example.test.models.listener.Listener;
import com.example.test.models.listener.ListenerFirebaseAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
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

    CommentAPI(){}

    private static CollectionReference commentsCollection = FirebaseFirestore.getInstance()
            .collection("comments");

    public String generateCommentId(){
        return commentsCollection.document().getId();
    }

    public void addComment(Comment comment, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        commentsCollection.document(comment.get_id()).set(comment)
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

    public void getComment(String commentId, Consumer<Comment> onComplete, Consumer<Exception> onFailure){
        commentsCollection.document(commentId).get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Comment comment = documentSnapshot.toObject(Comment.class);
                        onComplete.accept(comment);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        onFailure.accept(e);
                    }
                });
    }


    public Listener listenComment(String commentId, Consumer<Comment> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration = commentsCollection.document(commentId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
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
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenComments(Consumer<List<Comment>> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration = commentsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
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
            }
        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenCommentsChanges(Consumer<Comment> onAdded, Consumer<Comment> onModified, Consumer<Comment> onRemoved, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration = commentsCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
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



    public void getComments(Consumer<List<Comment>> onComplete, Consumer<Exception> onFailure) {
        commentsCollection.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<Comment> comments = new ArrayList<>();
                for (DocumentSnapshot document : documents) {
                    comments.add(document.toObject(Comment.class));
                }
                onComplete.accept(comments);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }

    public void deleteComment(String commentId, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        commentsCollection.document(commentId).delete()
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

    public void updateComment(Comment comment, Consumer<Void> onComplete, Consumer<Exception> onFailure){
        commentsCollection.document(comment.get_id()).set(comment)
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

    public Listener listenCommentsFromPost(String postId, Consumer<List<Comment>> onComplete, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration =
                commentsCollection.whereEqualTo("_postId",postId)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
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
                            }
                        });
        return new ListenerFirebaseAdapter(listenerRegistration);
    }

    public Listener listenCommentsChangesFromPost(String postId,Consumer<Comment> onAdded, Consumer<Comment> onModified, Consumer<Comment> onRemoved, Consumer<Exception> onFailure){
        ListenerRegistration listenerRegistration =
                commentsCollection.whereEqualTo("_postId",postId)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
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



    public void getCommentsFromPost(String postId,Consumer<List<Comment>> onComplete, Consumer<Exception> onFailure) {
        commentsCollection.whereEqualTo("_postId",postId).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                List<Comment> comments = new ArrayList<>();
                for (DocumentSnapshot document : documents) {
                    comments.add(document.toObject(Comment.class));
                }
                onComplete.accept(comments);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                onFailure.accept(e);
            }
        });
    }



}
