package com.example.susach.activities;

import com.example.susach.models.Event;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

//Dat: Helper class for Home logic (search, sort)
public class HomeLogicHelper {
    //Dat: Search events by keyword
    public static List<Event> searchEvents(String keyword, List<Event> events) {
        if (keyword == null || keyword.trim().isEmpty()) return events;
        List<Event> result = new ArrayList<>();
        for (Event e : events) {
            if (e.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                e.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                result.add(e);
            }
        }
        return result;
    }

    //Dat: Sort events by type
    public enum SortType { DATE, POPULARITY, VIEWS }

    public static List<Event> sortEvents(List<Event> events, SortType type) {
        List<Event> sorted = new ArrayList<>(events);
        switch (type) {
            case DATE:
                Collections.sort(sorted, Comparator.comparingInt(Event::getStartDate));
                break;
            case POPULARITY:

                //Collections.sort(sorted, Comparator.comparingInt(Event::getPopularity).reversed());
                break;
            case VIEWS:

                //Collections.sort(sorted, Comparator.comparingInt(Event::getViews).reversed());
                break;
        }
        return sorted;
    }
} 