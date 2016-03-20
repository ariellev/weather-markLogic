package com.markLogic.weather;

/**
 * Created by ariellev on 20.03.16.
 */
public interface IWeatherService {
    String getEvent(String id);

    Event[] getEventsByType(String type);
}
