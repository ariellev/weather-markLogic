package com.markLogic.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * Created by ariellev on 01.04.16.
 */
@CrossOrigin
@RestController
@EnableAutoConfiguration(exclude = {JacksonAutoConfiguration.class})
@ComponentScan({"com.markLogic.weather"})
@RequestMapping("weather/v1/places")
public class PlaceController {
    private static final Logger logger = LoggerFactory.getLogger(PlaceController.class);

    @Autowired
    @Qualifier("weatherService")
    private WeatherService service;

    @PostConstruct
    private void init() {
        logger.info("init");
        logger.info("service={}", service);
    }

    @RequestMapping(value = "/**", method = RequestMethod.GET)
    Place getPlace(@RequestParam(value = "name", required = true) String name) {
        logger.info("getPlace, name=" + name);
        Place place = service.getPlace(name);
        return place;
    }
}
