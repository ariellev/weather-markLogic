package com.markLogic.weather;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ariellev on 01.04.16.
 */
public class GeoQueryParserTest {

    //{"place":"New York City", "state":"New York", "state_code":"NY", "latitude":40.7069, "longitude":-73.6731}

    private class MockPlaceBuilder implements IPlaceBuilder {

        private Place place;

        public MockPlaceBuilder(String json) {
            place = parse(json);
        }

        @Override
        public Place getPlace(String placeName) {
            return this.place;
        }

        @Override
        public Place parse(String json) {
            Gson gson = new Gson();
            Place place = gson.fromJson(json, Place.class);
            return place;
        }
    }

    @Test
    public void testSimpleGeo() {
        GeoQueryParser parser = new GeoQueryParser("IN (500, 30.4, -88.12)");
        Assert.assertEquals(30.4, parser.getLat(), 0);
        Assert.assertEquals(-88.12, parser.getLon(), 0);
        Assert.assertEquals(500, parser.getRadius(), 0);
        Assert.assertTrue(parser.isGeoQuery());
        Assert.assertEquals("geo:\"@500 30.4,-88.12\"", parser.format());
    }

    @Test
    public void testPlace() {
        MockPlaceBuilder mockPlaceBuilder = new MockPlaceBuilder("{\"place\":\"New York City\", \"state\":\"New York\", \"state_code\":\"NY\", \"latitude\":40.7069, \"longitude\":-73.6731}");
        GeoQueryParser parser = new GeoQueryParser(mockPlaceBuilder, "in 80, New York City");
        Assert.assertEquals(40.7069, parser.getLat(), 0);
        Assert.assertEquals(-73.6731, parser.getLon(), 0);
        Assert.assertEquals(80, parser.getRadius(), 0);
        Assert.assertTrue(parser.isGeoQuery());
        Assert.assertEquals("geo:\"@80 40.7069,-73.6731\"", parser.format());

        parser = new GeoQueryParser(mockPlaceBuilder, "in New York City");
        Assert.assertEquals(40.7069, parser.getLat(), 0);
        Assert.assertEquals(-73.6731, parser.getLon(), 0);
        Assert.assertEquals(50, parser.getRadius(), 0);
        Assert.assertTrue(parser.isGeoQuery());
        Assert.assertEquals("geo:\"@50 40.7069,-73.6731\"", parser.format());
    }

    @Test
    public void testExtractGeoQuery() {
        String q = "before in 80, New York City)  AND injuries GT 50";
        GeoQueryParser parser = new GeoQueryParser(q);
        Assert.assertEquals("80, New York City", parser.extractGeoQuery(q));

        q = "before IN (500, 30.4, -88.12) and something else";
        Assert.assertEquals("500, 30.4, -88.12", parser.extractGeoQuery(q));

        q = "in New York City";
        Assert.assertEquals("New York City", parser.extractGeoQuery(q));
    }

    //@Test
    public void testGetGeoQuery() {
        MockPlaceBuilder mockPlaceBuilder = new MockPlaceBuilder("{\"place\":\"New York City\", \"state\":\"New York\", \"state_code\":\"NY\", \"latitude\":40.7069, \"longitude\":-73.6731}");
        GeoQueryParser parser = new GeoQueryParser(mockPlaceBuilder, "(in 80, New York City) AND injuries GT 50");
        Assert.assertTrue(parser.isGeoQuery());
    }
}
