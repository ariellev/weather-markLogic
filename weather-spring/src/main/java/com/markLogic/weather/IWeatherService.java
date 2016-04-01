package com.markLogic.weather;

/**
 * Created by ariellev on 20.03.16.
 */
public interface IWeatherService {
    String getEvent(String id);

    Event[] getEventsByType(String type);

    Event[] searchEvents(String queryString, Long fromDate, Long toDate, String type, String state, long start, int pageLength);

    Event[] searchEventsByPlace(String place, Long fromDate, Long toDate, String type, String state, long start, int pageLength);

    void updateEvent(Event event);

    void deleteEvent(String id);

    Place getPlace(String name);
}
