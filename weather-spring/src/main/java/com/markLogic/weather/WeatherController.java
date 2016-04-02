package com.markLogic.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
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
    Event[] searchEvents(@RequestBody SearchPayload payload,
                         @RequestParam(value = "pageLength", required = false, defaultValue = "10") int pageLength,
                         @RequestParam(value = "start", required = false, defaultValue = "0") int pageNum) {
        logger.info("searchEvents, payload=" + payload);
        logger.info("searchEvents, pageNum={}, pageLength={}, ", pageNum, pageLength);
        Event[] events = service.searchEvents(payload.getQuery(), payload.getFromDate(), payload.getToDate(), payload.getEventType(), payload.getState(), pageNum, pageLength);
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

    @RequestMapping(value = "/mock/**", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    String getMockEvents() {
        logger.info("getMockEvents");
        return "{\"data\":[{\"id\":\"75099_5223f61343120abf4127b2fdd057f59d533fed34a4176bead432f06801f2b75699a453a0c2b0c4baab8de2fa0933e015970b5d8c0f58eaace92a084d70c0ce6a\",\"event_type\":\"THUNDERSTORM WIND\",\"fatalities\":0,\"injuries\":0,\"total_prop\":0,\"total_crop\":0,\"prop_dmg\":0,\"prop_dmg_exp\":0,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"\",\"latitude\":\"45.19\",\"longitude\":\"-96.27\",\"date\":\"1979-06-19\",\"epoch_time\":298598400000,\"time\":\"1825\",\"state\":\"MN\",\"year\":1979},{\"id\":\"248149_cc254aee63f0f4ee6364cb7281af6774adb742670fa52ff06094ad70e55057af6141de54e7c8b006e190fb0d80c074513249f91f8d09d0d38eb61cc4664b487d\",\"event_type\":\"THUNDERSTORM WIND\",\"fatalities\":0,\"injuries\":0,\"total_prop\":0,\"total_crop\":0,\"prop_dmg\":0,\"prop_dmg_exp\":0,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"A line of severe thunderstorms raced across the region downing trees and powerlines.  Widespread tree damage was reported in Duplin county.  The Cedar Island ferry terminal reported a gust to 77 mph and Ocracoke village saw a gust to 73 mph.\",\"latitude\":\"34.59\",\"longitude\":\"-76.22\",\"date\":\"1998-06-30\",\"epoch_time\":899164800000,\"time\":\"10:00:00 PM\",\"state\":\"NC\",\"year\":1998},{\"id\":\"294915_df9024c0664b047f15db248d1bd86245dd90fd412b889a7cc50854ac9fdc0ab3085e9d9862124b6a3155020e372681eff84da628fb41c7f99cc3cd60f4cc138b\",\"event_type\":\"TORNADO\",\"fatalities\":0,\"injuries\":0,\"total_prop\":0,\"total_crop\":0,\"prop_dmg\":0,\"prop_dmg_exp\":0,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"A waterspout that formed over Currituck Sound came ashore and crossed Currituck Beach before heading into the Atlantic Ocean. Winds were measured at 50 mph as the tornado passed near the Currituck Beach Lighthouse but no significant damage occurred.\\n\",\"latitude\":\"36.23\",\"longitude\":\"-75.49\",\"date\":\"2000-07-24\",\"epoch_time\":964396800000,\"time\":\"12:57:00 PM\",\"state\":\"NC\",\"year\":2000},{\"id\":\"146615_faa31da32d3a598f3e39316082ca235ceaaa8f7182ab90110ccae1f53144eb0e6be25e7c214655c8ac02b0ce5feb95f92baac7710a07a98f9cd7e04f1c2b3cd3\",\"event_type\":\"THUNDERSTORM WIND\",\"fatalities\":0,\"injuries\":0,\"total_prop\":0,\"total_crop\":0,\"prop_dmg\":0,\"prop_dmg_exp\":0,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"\",\"latitude\":\"36\",\"longitude\":\"-88.54\",\"date\":\"1972-03-01\",\"epoch_time\":68256000000,\"time\":\"2320\",\"state\":\"TN\",\"year\":1972},{\"id\":\"114332_cca51ee13175e36877e97f5d715f497962535210df0a1819636a040a0f7246cd59f3d261a87fe80ed0de39187701939eeba8705fdd05c4c0e0186c580deff563\",\"event_type\":\"HAIL\",\"fatalities\":0,\"injuries\":0,\"total_prop\":0,\"total_crop\":0,\"prop_dmg\":0,\"prop_dmg_exp\":0,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"\",\"latitude\":\"46.07\",\"longitude\":\"-97.38\",\"date\":\"1989-05-29\",\"epoch_time\":612403200000,\"time\":\"1030\",\"state\":\"ND\",\"year\":1989},{\"id\":\"297588_b2c61374f9941cc5d4c75b2e9ceed5df799f2d5c2034c092464cbd2358affce2278b896d3cf89efe1700b2feca7d3d5311fc3e00a145a1eb828feba5a4690092\",\"event_type\":\"HAIL\",\"fatalities\":0,\"injuries\":0,\"total_prop\":0,\"total_crop\":0,\"prop_dmg\":0,\"prop_dmg_exp\":0,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"During the afternoon and early evening of the 26th severe thunderstorms formed over central and eastern Oklahoma. In addition to very large hail, numerous tornadoes were spawned, 2 of which occurred in the Norman Forecast area. See Tulsa storm data for tornadoes across eastern Oklahoma.\",\"latitude\":\"34.32\",\"longitude\":\"-95.47\",\"date\":\"2000-03-26\",\"epoch_time\":954028800000,\"time\":\"07:30:00 PM\",\"state\":\"OK\",\"year\":2000},{\"id\":\"22193_125a01de2c6052825d197dd771cda5edc3d7373e5165aba53a3d381a648b1af1093e5e389b81bf83bc4a2daf5be837a95b83b1227bebb93219e9363512307a87\",\"event_type\":\"TORNADO\",\"fatalities\":0,\"injuries\":0,\"total_prop\":25000,\"total_crop\":0,\"prop_dmg\":25,\"prop_dmg_exp\":3,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"\",\"latitude\":\"34.23\",\"longitude\":\"-84.26\",\"date\":\"1959-02-10\",\"epoch_time\":-343699200000,\"time\":\"1615\",\"state\":\"GA\",\"year\":1959},{\"id\":\"228056_381c6cab9414d810eca0a813a82a608ae34ec5cf275b26becbb0d42bc137ac17614486c19dcb8f0b7c4f1acb2b3459bc836973d9762f1bfbeeed6ba2aab7fda9\",\"event_type\":\"HAIL\",\"fatalities\":0,\"injuries\":0,\"total_prop\":0,\"total_crop\":0,\"prop_dmg\":0,\"prop_dmg_exp\":0,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"The Hale County Sheriff's Office reported a severe thunderstorm produced golfball-sized hail near Petersburg.\\n\",\"latitude\":\"33.56\",\"longitude\":\"-101.41\",\"date\":\"1997-05-18\",\"epoch_time\":863913600000,\"time\":\"06:38:00 PM\",\"state\":\"TX\",\"year\":1997},{\"id\":\"20418_79e8776b3dc0afc1ede65eed7c590ef4847f7db7dcce96068893dea298296cf4d122951edb92e711135e303b6d93f3627a3752493c72d4973f0864b67d1986f6\",\"event_type\":\"TORNADO\",\"fatalities\":0,\"injuries\":0,\"total_prop\":2500,\"total_crop\":0,\"prop_dmg\":2,\"prop_dmg_exp\":3,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"\",\"latitude\":\"29.1\",\"longitude\":\"-81.32\",\"date\":\"1986-03-14\",\"epoch_time\":511142400000,\"time\":\"0630\",\"state\":\"FL\",\"year\":1986},{\"id\":\"108941_f116e8f03962e44b038f304cbfa26d0e2b3413573adf4f73ba89315f384a8d6054bd811ff03b49d5e09ec427cdf6efa0b490b8228cec59f8b2f248228587a294\",\"event_type\":\"THUNDERSTORM WIND\",\"fatalities\":0,\"injuries\":0,\"total_prop\":0,\"total_crop\":0,\"prop_dmg\":0,\"prop_dmg_exp\":0,\"crop_dmg\":0,\"crop_dmg_exp\":0,\"remarks\":\"\",\"latitude\":\"35.08\",\"longitude\":\"-84.03\",\"date\":\"1985-06-07\",\"epoch_time\":486950400000,\"time\":\"1400\",\"state\":\"NC\",\"year\":1985}],\"status\":200,\"config\":{\"method\":\"POST\",\"transformRequest\":[null],\"transformResponse\":[null],\"url\":\"http://localhost:8080/weather/v1/events/search/?\",\"data\":{\"query\":\"\",\"fromDate\":-628477200000,\"toDate\":1459582672068,\"state\":\"\",\"eventType\":\"\"},\"headers\":{\"Accept\":\"application/json, text/plain, */*\",\"Content-Type\":\"application/json;charset=utf-8\"}},\"statusText\":\"OK\"}";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(WeatherController.class, args);
    }

}