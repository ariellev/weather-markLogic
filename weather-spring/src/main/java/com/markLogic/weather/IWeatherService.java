package com.markLogic.weather;

/**
 * Created by ariellev on 20.03.16.
 */
public interface IWeatherService {
    String getEvent(String id);

    Event[] getEventsByType(String type);

    Event[] searchEvents(String text, Long fromDate, Long toDate, String type, String state, long start, int pageLength);
}
