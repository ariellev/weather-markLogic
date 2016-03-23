package com.markLogic.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@CrossOrigin
@RestController
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
@ComponentScan({"com.markLogic.weather"})
@RequestMapping("weather/v1/events")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    @Qualifier("weatherService")
    private WeatherService service;

    @PostConstruct
    private void init() {
        logger.info("init");
        logger.info("service={}", service);
    }

    public void setService(WeatherService service) {
        this.service = service;
    }

    @RequestMapping("/hello")
    String hello() {
        return "Hello World!";
    }


    @RequestMapping(value = "/id/{id}/**", method = RequestMethod.GET)
    String getEvent(@PathVariable(value = "id") String id) {
        logger.info("getEvent, id=" + id);
        String event = service.getEvent(id);
        return event;
    }

    @RequestMapping(value = "/{type}/**", method = RequestMethod.GET)
    Event[] getEventByType(@PathVariable(value = "type") String type) {
        logger.info("getEventByType, type=" + type);
        Event[] events = service.getEventsByType(type);
        return events;
    }

    @RequestMapping(value = "/search/**", method = RequestMethod.POST)
    Event[] searchEvents(@RequestBody SearchPayload payload) {
        logger.info("searchEvents, payload=" + payload);
        Event[] events = service.searchEvents(payload.getQuery(), payload.getFromDate(), payload.getToDate(), payload.getEventType(), payload.getState(), 0, 10);
        return events;
    }

    @RequestMapping(value = "/delete/{id}/**", method = RequestMethod.DELETE)
    void deleteEvent(@PathVariable(value = "id") String id) {
        logger.info("deleteEvent, id=" + id);
        service.deleteEvent(id);
    }

    @RequestMapping(value = "/update/**", method = RequestMethod.POST)
    void updateEvent(@RequestBody Event event) {
        logger.info("updateEvent, event=" + event);
        service.updateEvent(event);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WeatherController.class, args);
    }

}