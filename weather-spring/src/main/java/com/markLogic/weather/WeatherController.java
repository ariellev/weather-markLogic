package com.markLogic.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

@RestController
@EnableAutoConfiguration
@ComponentScan({"com.markLogic.weather"})
@RequestMapping("weather/v1/events")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    @Autowired
    @Qualifier("weatherService")
    private IWeatherService service;

    @PostConstruct
    private void init() {
        logger.info("init");
        logger.info("service={}", service);
    }

    public void setService(IWeatherService service) {
        this.service = service;
    }

    @RequestMapping("/hello")
    String hello() {
        return "Hello World!";
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getEvent(@RequestParam(value = "id", required = true) String id) {
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

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WeatherController.class, args);
    }

}