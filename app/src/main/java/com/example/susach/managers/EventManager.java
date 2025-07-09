package com.example.susach.managers;

import com.example.susach.models.Event;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class EventManager {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface EventListCallback {
        void onSuccess(List<Event> events);
        void onFailure(Exception e);
    }

    public void getAllEvents(EventListCallback callback) {
        db.collection("events").get()
            .addOnSuccessListener(queryDocumentSnapshots -> {
                List<Event> events = queryDocumentSnapshots.toObjects(Event.class);
                callback.onSuccess(events);
            })
            .addOnFailureListener(callback::onFailure);
    }

    public interface EventCallback {
        void onSuccess(Event event);
        void onFailure(Exception e);
    }

    public void getEventById(String eventId, EventCallback callback) {
        db.collection("events").document(eventId).get()
            .addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Event event = documentSnapshot.toObject(Event.class);
                    callback.onSuccess(event);
                } else {
                    callback.onFailure(new Exception("Không tìm thấy sự kiện!"));
                }
            })
            .addOnFailureListener(callback::onFailure);
    }

    public interface SimpleCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public void saveEvent(Event event, boolean isAdd, SimpleCallback callback) {
        db.collection("events").document(event.getId()).get().addOnSuccessListener(document -> {
            String createBy = event.getCreateBy();
            if (!isAdd && document.exists()) {
                Event oldEvent = document.toObject(Event.class);
                if (oldEvent != null && oldEvent.getCreateBy() != null) {
                    createBy = oldEvent.getCreateBy();
                }
            }
            event.setCreateBy(createBy);
            db.collection("events").document(event.getId()).set(event)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
        }).addOnFailureListener(callback::onFailure);
    }

    public void deleteEvent(String eventId, SimpleCallback callback) {
        db.collection("events").document(eventId).delete()
            .addOnSuccessListener(aVoid -> callback.onSuccess())
            .addOnFailureListener(callback::onFailure);
    }
} 