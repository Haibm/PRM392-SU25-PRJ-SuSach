package com.example.susach.firebase;

import com.example.susach.models.Event;
import com.google.firebase.firestore.*;
import java.util.*;

public class EventData {
    private static final String COLLECTION = "events";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface EventListCallback {
        void onSuccess(List<Event> events);
        void onFailure(Exception e);
    }
    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public static void getAllEvents(EventListCallback callback) {
        db.collection(COLLECTION).get()
            .addOnSuccessListener(querySnapshot -> {
                List<Event> events = new ArrayList<>();
                for (DocumentSnapshot doc : querySnapshot) {
                    Event event = doc.toObject(Event.class);
                    if (event != null) events.add(event);
                }
                callback.onSuccess(events);
            })
            .addOnFailureListener(callback::onFailure);
    }

    public static void addEvent(Event event, SimpleCallback callback) {
        db.collection(COLLECTION).document(event.getId())
            .set(event)
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }

    public static void updateEvent(Event event, SimpleCallback callback) {
        db.collection(COLLECTION).document(event.getId())
            .set(event, SetOptions.merge())
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }

    public static void deleteEvent(String eventId, SimpleCallback callback) {
        db.collection(COLLECTION).document(eventId)
            .delete()
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }
} 